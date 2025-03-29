/***
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.controllers;

import com.monge.sevenexpress.dto.NewOrderRequest;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.BusinessQuoteRequest;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.PaymentReceiptRequest;
import com.monge.sevenexpress.subservices.OrdersService;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.dto.OrderDTO;
import com.monge.sevenexpress.enums.OrderType;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.services.OrdersControlService;
import com.monge.sevenexpress.services.UserService;
import com.monge.sevenexpress.services.UtilitiesService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/business") // Prefijo para todas las rutas
public class BusinesController {

   
    @Autowired
    private UserService userService;

    @Autowired
    private OrdersControlService ordersControlService;
    
    @Autowired
    private UtilitiesService utilitiesService;
    
    @Autowired
    private ContabilityService contabilityService;

    // Endpoint para procesar la cotización con los parámetros de dirección y posición
    @GetMapping("/quote")
    public ResponseEntity<ApiResponse> getDeliveryQuote(@ModelAttribute BusinessQuoteRequest bqr) { // Agregamos el parámetro Principal

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());

        try {
            // Verificamos si el principal (usuario) está autenticado
            if (business == null) {
                // Regresamos una respuesta de error si el usuario no está autenticado

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Usuario no autorizado"));
            }

            bqr.setRequester(business);

            // Llamamos a la lógica para calcular el costo de entrega
            double deliveryCost = utilitiesService.getGoogleMapsService().calculateDeliveryCost(bqr);

            // Regresamos una respuesta exitosa con el costo de la entrega
            return ResponseEntity.ok().body(ApiResponse.success("Costo de entrega calculado con éxito", deliveryCost));

        } catch (Exception e) {
            // Regresamos una respuesta de error en caso de excepciones

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error al calcular el costo de entrega: " + e.getMessage()));
        }
    }

    @GetMapping("/myorders")
    public ResponseEntity<ApiResponse> getMyOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Business not found"));
        }

        List<Order> orders = ordersControlService.getOrdersService().getOrdersByBusinessId(business.getId());
        return ResponseEntity.ok(ApiResponse.success("success", orders.stream().map(OrderDTO::new).collect(Collectors.toList())));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> createOrder(
            @RequestBody NewOrderRequest newOrder) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());

        if (business == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Usuario no autenticado"));
        }

        if (business.getAccountStatus().equals(Business.AccountStatus.DESACTIVADO)
                || business.getAccountStatus().equals(Business.AccountStatus.SUSPENDIDO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Estas " + business.getAccountStatus().name() + " no puedes enviar ordenes"));
        }

        Customer customer = userService.getCustomerService().findByPhoneNumber(newOrder.getCustomerPhone());

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Cliente no encontrado"));
        }

        OrderType orderType = newOrder.getOrderType();
        Order order = new Order(business, customer, newOrder);
        order.setOrderType(orderType);

        ordersControlService.addOrder(order);

        // orderRepository.save(order);
        return ResponseEntity.ok(ApiResponse.success("Orden creada exitosamente", new OrderDTO(order)));
    }

    /**
     * Endpoint para obtener un Business con sus relaciones (BalanceAccount y
     * BusinessContract)
     */
    @GetMapping("/account")
    public ResponseEntity<ApiResponse> getBusinessAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());


        return ResponseEntity.ok(ApiResponse.success("your business account", business));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Quitamos "Bearer " del token
        }

        utilitiesService.getTokenBlacklistService().addToBlacklist(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente", null));

    }

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> changeOrderStatus(@RequestBody ChangeOrderStatusRequest cosr) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());
        cosr.setRequester(business);

        ApiResponse result = ordersControlService.changeOrderStatus(cosr);
        return ResponseEntity.ok(result);

    }

    // Endpoint para obtener sugerencias de direcciones
    @GetMapping("/getTransactions")
    public ResponseEntity<ApiResponse> getTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());

        List<Transaction> last10Transactions = contabilityService.getTransactionService().getLast10Transactions(business.getId());
        return ResponseEntity.ok(ApiResponse.success("Transactions", last10Transactions));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> getCustomerByPhone(@RequestParam("phone") String phone) {
        Customer customer = userService.getCustomerService().findByPhoneNumber(phone);
        if (customer == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("No se encontro este cliente."));

        }
        return ResponseEntity.ok(ApiResponse.success("customer found!", customer));
    }

    @PostMapping("/createOrUpdate")
    public ResponseEntity<ApiResponse> createOrUpdateCustomer(@RequestBody Customer customer) {
        // Validar que phoneNumber no sea nulo o vacío
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El número de teléfono es obligatorio"));
        }

        // Limpiar phoneNumber (remover caracteres no numéricos)
        String cleanedPhoneNumber = customer.getPhoneNumber().replaceAll("[^0-9]", "");

        // Validar que el phoneNumber tenga exactamente 10 dígitos
        if (cleanedPhoneNumber.length() != 10) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El número de teléfono debe tener 10 dígitos"));
        }

        // Validar que name y address no sean nulos o vacíos
        if (customer.getName() == null || customer.getName().trim().isEmpty()
                || customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El nombre y la dirección son obligatorios"));
        }

        // Establecer el número de teléfono limpio
        customer.setPhoneNumber(cleanedPhoneNumber);

        // Llamar al método merge del servicio
        Customer savedCustomer = userService.getCustomerService().merge(customer);
        // Retornar la respuesta con el cliente en formato JSON
        return ResponseEntity.ok(ApiResponse.success("customer saved!", savedCustomer));
    }

    // Endpoint para obtener sugerencias de direcciones
    @Cacheable(value = "suggestions", key = "#input", unless = "#result == null || #result.body.data.isEmpty()")
    @GetMapping("/getSuggestions")
    public ResponseEntity<ApiResponse> getSuggestions(@RequestParam String input) {
        List<String> suggestions = utilitiesService.getGoogleMapsService().getSuggestions(input);
        return ResponseEntity.ok(ApiResponse.success("Sugerencias", suggestions));
    }

    // Endpoint para obtener coordenadas (lat, lng) de una dirección
    @Cacheable(value = "addressToPosition", key = "#address", unless = "#result == null || #result.body.data == null")
    @GetMapping("/addressToPosition")
    public ResponseEntity<ApiResponse> addressToPosition(@RequestParam String address) {
        String coordinates = utilitiesService.getGoogleMapsService().addressToPosition(address);
        if (coordinates != null) {
            return ResponseEntity.ok(ApiResponse.success("Coordenadas", coordinates));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("No se pudo obtener las coordenadas"));
        }
    }

    // Endpoint para obtener sugerencias de direcciones
    @GetMapping("/systemStatus")
    public ResponseEntity<ApiResponse> systemStatus() {

        long inProcessOrders = ordersControlService.getInProcessOrders().size();
        long connectedDeliveries = ordersControlService.getConectedDeliveries().size();

        // Calcular la capacidad total de los repartidores (3 órdenes por repartidor)
        long totalCapacity = connectedDeliveries * 3;

        // Calcular la puntuación de saturación
        double saturationScore = totalCapacity > 0 ? (double) inProcessOrders / totalCapacity : 0.0;
        
        if(connectedDeliveries==0){
        saturationScore = 3;
        }

        // Crear el HashMap con los valores
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put("inProcessOrders", inProcessOrders);
        systemStatus.put("connectedDeliveries", connectedDeliveries);
        systemStatus.put("saturationScore", saturationScore); // Puntuación de saturación

        return ResponseEntity.ok(ApiResponse.success("Estado del sistema", systemStatus));

    }

    @PostMapping("/createPaymentReceipt")
    public ResponseEntity<ApiResponse> createPaymentReceipt(@RequestBody PaymentReceiptRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Business business = userService.getBusinessByUserName(authentication.getName());
        

        // Check if balanceAccount is null
        if (business.getBalanceAccount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Balance account not found"));
        }

        request.setRequester(business);
        request.setStatus(PaymentReceipt.PaymentStatus.PENDING);
        request.setBalanceAccountId(business.getBalanceAccount().getId());
        PaymentReceipt receipt = contabilityService.getPaymentReceiptService().savePaymentReceipt(request);
        return ResponseEntity.ok(ApiResponse.success("Comprobante recibido,", receipt));
    }

}
