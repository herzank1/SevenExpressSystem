/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.controllers;

import com.monge.sevenexpress.dto.AdminUpdateUserBusinessRequest;
import com.monge.sevenexpress.dto.AdminOrderSetDelivery;
import com.monge.sevenexpress.dto.AdminRefreshCache;
import com.monge.sevenexpress.dto.AdminRefreshCache.RefreshAction;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.services.AdminService;
import com.monge.sevenexpress.services.AdminDataBaseService;
import com.monge.sevenexpress.services.BusinessService;
import com.monge.sevenexpress.services.CustomerService;
import com.monge.sevenexpress.services.GoogleMapsService;
import com.monge.sevenexpress.services.OrderAsignatorService;
import com.monge.sevenexpress.services.OrdersService;
import com.monge.sevenexpress.services.TokenBlacklistService;
import com.monge.sevenexpress.services.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admins") // Prefijo para todas las rutas
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private GoogleMapsService googleMapsService;

    @Autowired
    private OrderAsignatorService orderAsignatorService;

    @Autowired
    private AdminDataBaseService adminDataBaseService;

    /**
     * *
     *
     * @return todas las ordenes en la lista
     */
    @GetMapping("/myorders")
    public ResponseEntity<ApiResponse> getMyOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Admin admin = adminService.getByUserName(authentication.getName());

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("admin not found"));
        }

        List<Order> orders = ordersService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("success", orders));
    }

    /**
     * Endpoint para obtener un Business con sus relaciones (BalanceAccount y
     * BusinessContract)
     */
    @GetMapping("/account")
    public ResponseEntity<ApiResponse> getAdminAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Admin admin = adminService.getByUserName(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success("your business account", admin));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<ApiResponse> getDeliveries() {
        return ResponseEntity.ok(ApiResponse.success("sucess", orderAsignatorService.getDeliveryService().getConectedDeliveries()));

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Quitamos "Bearer " del token
        }

        tokenBlacklistService.addToBlacklist(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente", null));

    }

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> changeOrderStatus(@RequestBody ChangeOrderStatusRequest cosr) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Admin admin = adminService.getByUserName(authentication.getName());
        cosr.setRequester(admin);

        ApiResponse result = ordersService.changeOrderStatus(cosr);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/orderSetDelivery")
    public ResponseEntity<ApiResponse> orderSetDelivery(@RequestBody AdminOrderSetDelivery aosd) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Admin admin = adminService.getByUserName(authentication.getName());
        aosd.setRequester(admin);

        ApiResponse result = ordersService.orderSetDelivery(aosd);
        return ResponseEntity.ok(result);

    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> getCustomerByPhone(@RequestParam("phone") String phone) {
        Customer customer = customerService.findByPhoneNumber(phone);
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

        // Buscar si el cliente ya existe por phoneNumber
        Customer existingCustomerOpt = customerService.findByPhoneNumber(cleanedPhoneNumber);

        Customer savedCustomer;
        if (existingCustomerOpt != null) {
            // Si el cliente existe, actualizar solo los datos permitidos
            Customer existingCustomer = existingCustomerOpt;
            existingCustomer.setName(customer.getName());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setPosition(customer.getPosition());

            savedCustomer = customerService.save(existingCustomer);
        } else {
            // Si no existe, registrar uno nuevo
            customer.setPhoneNumber(cleanedPhoneNumber); // Guardar el número limpio
            savedCustomer = customerService.save(customer);
        }

        // Retornar la respuesta con el cliente en formato JSON
        return ResponseEntity.ok(ApiResponse.success("customer saved!", savedCustomer));
    }

    // Endpoint para obtener sugerencias de direcciones
    @Cacheable(value = "suggestions", key = "#input", unless = "#result == null || #result.body.data.isEmpty()")
    @GetMapping("/getSuggestions")
    public ResponseEntity<ApiResponse> getSuggestions(@RequestParam String input) {
        List<String> suggestions = googleMapsService.getSuggestions(input);
        return ResponseEntity.ok(ApiResponse.success("Sugerencias", suggestions));
    }

    // Endpoint para obtener coordenadas (lat, lng) de una dirección
    @Cacheable(value = "addressToPosition", key = "#address", unless = "#result == null || #result.body.data == null")
    @GetMapping("/addressToPosition")
    public ResponseEntity<ApiResponse> addressToPosition(@RequestParam String address) {
        String coordinates = googleMapsService.addressToPosition(address);
        if (coordinates != null) {
            return ResponseEntity.ok(ApiResponse.success("Coordenadas", coordinates));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("No se pudo obtener las coordenadas"));
        }
    }

    // Endpoint para obtener sugerencias de direcciones
    @GetMapping("/systemStatus")
    public ResponseEntity<ApiResponse> systemStatus() {

        long inProcessOrders = orderAsignatorService.getOrdersService().getInProcessOrders().count();
        long connectedDeliveries = orderAsignatorService.getDeliveryService().getConectedDeliveries().size();

        // Calcular la capacidad total de los repartidores (3 órdenes por repartidor)
        long totalCapacity = connectedDeliveries * 3;

        // Calcular la puntuación de saturación
        double saturationScore = totalCapacity > 0 ? (double) inProcessOrders / totalCapacity : 0.0;

        // Crear el HashMap con los valores
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put("inProcessOrders", inProcessOrders);
        systemStatus.put("connectedDeliveries", connectedDeliveries);
        systemStatus.put("saturationScore", saturationScore); // Puntuación de saturación

        return ResponseEntity.ok(ApiResponse.success("Estado del sistema", systemStatus));

    }

    @PostMapping("/updateUserBusinessData")
    @Deprecated
    public ResponseEntity<?> activateUser(@RequestBody AdminUpdateUserBusinessRequest request) {

        // Buscar al usuario por su ID
        User findByUserName = userService.findByUserName(request.getUserName());
        if (findByUserName == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found."));
        }

        // Activar la cuenta del usuario
        findByUserName.setActive(true);
        userService.save(findByUserName);

        return ResponseEntity.ok(ApiResponse.success("User activated successfully.", true));
    }

    @Deprecated
    @PostMapping("/refreshCache")
    public ResponseEntity<?> refreshCache(@RequestBody AdminRefreshCache request) {

        Optional.ofNullable(request.getId()).ifPresent(id -> {
            switch (request.getRefreshAction()) {

                case RefreshAction.USER:
                    userService.removeFromCache(request.getId());

                    break;

                case RefreshAction.BUSINESS:
                    businessService.removeFromCache(request.getId());

                    break;

                case RefreshAction.ADMIN:
                    adminService.removeFromCache(request.getId());

                    break;

                case RefreshAction.DELIVERY:

                    orderAsignatorService.getDeliveryService().removeFromCache(id);
                    break;

            }
        });

        return ResponseEntity.ok(ApiResponse.success("User activated successfully.", true));
    }

    @PostMapping("/updateEntity")
    public ResponseEntity<ApiResponse> updateEntity(@RequestParam String entityName, @RequestBody Object entity) {

        if (entity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Entity data is missing."));
        }

        // Validar el tipo de entidad
        if (!(entity instanceof User || entity instanceof Delivery || entity instanceof Business || entity instanceof Customer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid entity type."));
        }

        return adminDataBaseService.updateEntity(entity, entityName);
    }

    @GetMapping("/getEntityList")
    public ResponseEntity<ApiResponse> getEntityList(@RequestParam String entityName) {
        return adminDataBaseService.getEntityList(entityName);
    }

}
