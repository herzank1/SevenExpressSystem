/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.UserProfile;
import com.monge.sevenexpress.entities.dto.QuoteDTO;
import com.monge.sevenexpress.services.UtilitiesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * *
 * Este controlador tiene llamadas a la api de google maps la cuales generan un
 * costo por llamada
 *
 * @author DeliveryExpress
 */
@RestController
@RequestMapping("/api/v1")
public class UtilsController {

  

    @Autowired
    private UtilitiesService utilitiesService;

    @Autowired
    private AuthController authController;  // Inyectamos el authController

    // Endpoint para obtener sugerencias de direcciones
    @Cacheable(value = "suggestions", key = "#input", unless = "#result == null || #result.body.data.isEmpty()")
    @GetMapping("/utils/getAddressSuggestions")
    public ResponseEntity<ApiResponse> getAddressSuggestions(@RequestParam String input) {
        try {
            // Usamos el método del authController para obtener el usuario autenticado
            UserProfile anyAuthenticated = (UserProfile) authController.getAnyAuthenticated();
            List<String> suggestions = utilitiesService.getGoogleMapsService().getSuggestions(anyAuthenticated.getBalanceAccount().getId(),input);
         
            return ResponseEntity.ok(ApiResponse.success("Sugerencias", suggestions));
        } catch (Exception e) {
            // Si el usuario no está autenticado, manejamos el error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

    // Endpoint para obtener coordenadas (lat, lng) de una dirección
    @Cacheable(value = "addressToPosition", key = "#address", unless = "#result == null || #result.body.data == null")
    @GetMapping("/utils/addressToPosition")
    public ResponseEntity<ApiResponse> addressToPosition(@RequestParam String address) {
        try {
            // Usamos el método del authController para obtener el usuario autenticado
            UserProfile anyAuthenticated = (UserProfile) authController.getAnyAuthenticated();
            String coordinates = utilitiesService.getGoogleMapsService().addressToPosition(anyAuthenticated.getBalanceAccount().getId(),address);
            if (coordinates != null) {
                return ResponseEntity.ok(ApiResponse.success("Coordenadas", coordinates));
            } else {
                return ResponseEntity.status(404).body(ApiResponse.error("No se pudo obtener las coordenadas"));
            }
        } catch (Exception e) {
            // Si el usuario no está autenticado, manejamos el error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }
    }

    /**
     * *
     * El cotizante debera ser un business y en el cuerpo del Quote DTO debera
     * indicar to:direccion de destino y toPosition: de destino la funcion
     * agregara automaticamente from y fromPosition del business
     *
     * @param quoteDTO
     * @return
     */
    /*el costo a la api de google maps tiene un costo aprox 0.085 pesos mxn por llamada/elementos
    esta funcion realiza una llamada a DistanceMatrix*/
    @GetMapping("/utils/getQuote")
    public ResponseEntity<ApiResponse> getQuote(@ModelAttribute QuoteDTO quoteDTO) {
        try {
            // Usamos el método del authController para obtener el usuario autenticado
            Business business = authController.getAuthenticatedBusiness();  // Esto lanzará una excepción si no está autenticado

            quoteDTO.setFrom(business.getAddress());
            quoteDTO.setFromPosition(business.getPosition());

            QuoteDTO calculateDeliveryCost = utilitiesService.getGoogleMapsService().calculateDeliveryCost(quoteDTO, business.getBusinessContract()); // Aquí ya no se necesita 'business'

            // Regresamos una respuesta exitosa con el costo de la entrega
            return ResponseEntity.ok().body(ApiResponse.success("cotizacion", calculateDeliveryCost));
        } catch (Exception e) {
            // Si el usuario no está autenticado, manejamos el error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }


}
