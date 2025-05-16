
package com.monge.sevenexpress.subservices;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.TravelMode;
import com.monge.sevenexpress.entities.Address;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Distance;
import com.monge.sevenexpress.entities.dto.QuoteDTO;
import com.monge.sevenexpress.events.ChargeApiCallsEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class GoogleMapsService {

    private final ConcurrentHashMap<Long, Double> userApiUsage = new ConcurrentHashMap<>();

    final double quoteCost = 0.10;
    final double suggestionsCost = 0.10;//real 0.04811 MXN/call
    final double geoCoding = 0.20;//real 0.085 MXN/call

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AddressService AddressService;

    @Autowired
    private DistanceService distanceService;

    private GeoApiContext context;

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is missing!");
        }
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public List<String> getSuggestions(long balanceAccountId,String input) {
        List<String> suggestions = new ArrayList<>();

        // Validación: mínimo 2 palabras
        if (input == null || input.trim().split("\\s+").length < 2) {
            return suggestions; // Retorna lista vacía si no cumple
        }

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, input).await();
            for (GeocodingResult result : results) {
                suggestions.add(result.formattedAddress);
            }
            
             addConsume(balanceAccountId, suggestionsCost);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    public String addressToPosition(long balanceAccountId,String address) {

        Address _address = AddressService.getAddress(address);
        if (_address != null && _address.getCoordenates() != null) {
            return _address.getCoordenates();

        }

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;

                String coordenates = lat + "," + lng;
                String formatted = results[0].formattedAddress;

                _address = AddressService.registerGeocodingResult(address, coordenates, formatted);
                 addConsume(balanceAccountId, geoCoding);

                return _address.getCoordenates(); // Return coordinates as a string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 
     * @param balanceAccountId la cuenta del caller para cargar el gasto por uso de api
     * @param origin
     * @param destination
     * @return detalles de la  distancia de un punto a otro
     */
    public DistanceDetails getDistanceDetails(long balanceAccountId,String origin, String destination) {
        // Generar una instancia temporal para obtener ID ya normalizado
        Distance temp = new Distance(origin, destination, 0, 0); // Solo para generar el ID normalizado
        String id = temp.getId();
        Distance distance = distanceService.getDistance(id);

        // Buscar en caché o BD
        if (distance != null) {
            return new DistanceDetails(distance.getOrigin(), distance.getDestination(), distance.getDistanceKm(), distance.getDurationMin());
        }

        try {

            // Asegura que estén correctamente codificadas en UTF-8 para URLs
            String encodedOrigin = URLEncoder.encode(origin, StandardCharsets.UTF_8.toString());
            String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString());

            DistanceMatrix result = DistanceMatrixApi.getDistanceMatrix(context,
                    new String[]{encodedOrigin}, new String[]{encodedDestination})
                    .mode(TravelMode.DRIVING) // Puedes cambiarlo a WALKING, BICYCLING, TRANSIT
                    .await();

            if (result.rows.length > 0 && result.rows[0].elements.length > 0) {
                DistanceMatrixElement element = result.rows[0].elements[0];
                if (element.status == DistanceMatrixElementStatus.OK) {
                    double distanceKm = element.distance.inMeters / 1000.0;
                    double durationMin = element.duration.inSeconds / 60.0;

                    distance = distanceService.registerDistanceMAtrixResult(origin, destination, distanceKm, durationMin);
                    /*registrar el consumo*/
                    addConsume(balanceAccountId, quoteCost);

                    return new DistanceDetails(distance.getOrigin(), distance.getDestination(), distance.getDistanceKm(), distance.getDurationMin());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DistanceDetails(origin, destination, -1, -1); // Retorna valores -1 en caso de error
    }

    public QuoteDTO calculateDeliveryCost(QuoteDTO quote, BusinessContract contract) {

        DistanceDetails distanceDetails = getDistanceDetails(contract.getId(),quote.getFrom(), quote.getTo());
        quote.fill(distanceDetails);
        quote.calc(contract);

        return quote;

    }

    public void addConsume(long userId, double cantidad) {
      
            // Si no existe, lo inicializa con 0.0
        userApiUsage.putIfAbsent(userId, 0.0);

        // Luego le suma la cantidad al valor actual
        userApiUsage.compute(userId, (id, valorActual) -> valorActual + cantidad);

    
    }

    // Ejecutar todos los días a las 10:00 PM
    @Scheduled(cron = "0 0 22 * * ?")
    public void runChargeApiCalls() {

        ChargeApiCallsEvent event = new ChargeApiCallsEvent(this, userApiUsage);
        eventPublisher.publishEvent(event);

        userApiUsage.clear();
    }

    @Data
    public class DistanceDetails {

        private String origin;
        private String destination;
        private double kilometers;
        private double minutes;

        public DistanceDetails(String origin, String destination, double kilometers, double minutes) {
            this.origin = origin;
            this.destination = destination;
            this.kilometers = kilometers;
            this.minutes = minutes;
        }

        @Override
        public String toString() {
            return "DistanceDetails{"
                    + "origin='" + origin + '\''
                    + ", destination='" + destination + '\''
                    + ", kilometers=" + kilometers
                    + ", minutes=" + minutes
                    + '}';
        }

        /**
         * *
         * esta funcion debe retornar mas infomracion hacerca de la distancia
         *
         * @param bqr
         * @return
         */
    }
}
