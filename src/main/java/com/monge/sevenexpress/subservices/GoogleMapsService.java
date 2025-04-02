/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.TravelMode;
import com.monge.sevenexpress.dto.BusinessQuoteRequest;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.dto.QuoteDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private GeoApiContext context;

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API Key is missing!");
        }
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, input).await();
            for (GeocodingResult result : results) {
                suggestions.add(result.formattedAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    public String addressToPosition(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
            if (results.length > 0) {
                double lat = results[0].geometry.location.lat;
                double lng = results[0].geometry.location.lng;
                return lat + "," + lng; // Return coordinates as a string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DistanceDetails getDistanceDetails(String origin, String destination) {
        try {
            DistanceMatrix result = DistanceMatrixApi.getDistanceMatrix(context,
                    new String[]{origin}, new String[]{destination})
                    .mode(TravelMode.DRIVING) // Puedes cambiarlo a WALKING, BICYCLING, TRANSIT
                    .await();

            if (result.rows.length > 0 && result.rows[0].elements.length > 0) {
                DistanceMatrixElement element = result.rows[0].elements[0];
                if (element.status == DistanceMatrixElementStatus.OK) {
                    double distanceKm = element.distance.inMeters / 1000.0;
                    double durationMin = element.duration.inSeconds / 60.0;
                    return new DistanceDetails(origin, destination, distanceKm, durationMin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DistanceDetails(origin, destination, -1, -1); // Retorna valores -1 en caso de error
    }
    
         public QuoteDTO calculateDeliveryCost(QuoteDTO quote,BusinessContract contract) {
            
            DistanceDetails distanceDetails = getDistanceDetails(quote.getFrom(),quote.getTo());
            quote.fill(distanceDetails);
            quote.calc(contract);

            return quote;

        }
    
     public double calculateDeliveryCost(BusinessQuoteRequest bqr) {
            
            DistanceDetails distanceDetails = getDistanceDetails(bqr.getAddress(),bqr.getAddress());

            if (distanceDetails.getKilometers() == -1) {
                return 50.0;
            } else {
          

                return 40;
            }

        }

    @Data
    public  class DistanceDetails {

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
