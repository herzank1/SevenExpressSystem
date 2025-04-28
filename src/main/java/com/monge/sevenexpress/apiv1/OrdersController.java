/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.AdminOrderSetDelivery;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.DeliveryGetAvailableOrderRequest;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import com.monge.sevenexpress.dto.NewOrderRequest;
import com.monge.sevenexpress.dto.NewTestOrderRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.entities.dto.OrderDTO;
import com.monge.sevenexpress.enums.OrderType;
import com.monge.sevenexpress.services.OrdersControlService;
import com.monge.sevenexpress.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@RequestMapping("/api/v1") // Prefijo para todas las rutas
public class OrdersController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersControlService ordersControlService;

    @Autowired
    AuthController authController;

    @PostMapping("/orders/changeStatus")
    public ResponseEntity<ApiResponse> changeOrderStatus(@RequestBody ChangeOrderStatusRequest cosr) {

        User requester = authController.getAuthenticatedUser();

        cosr.setRequester(requester);

        ApiResponse result = ordersControlService.changeOrderStatus(cosr);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders/getAvailables")
    public ResponseEntity<ApiResponse> getMyOrders(@RequestBody DeliveryGetAvailableOrderRequest params) {
        Delivery anyAuthenticated;

        try {
            // Obtener el usuario autenticado y el tipo de cuenta (Business, Delivery, Admin)
            anyAuthenticated = authController.getAuthenticatedDelivery();
        } catch (Exception e) {
            // Si ocurre un error al obtener la cuenta autenticada, se maneja aquí
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }

        if (anyAuthenticated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Account is not authenticated"));
        }

        List<Order> orders = new ArrayList<>();

        orders = ordersControlService.getOrdersService().getAvailableOrders(params);

        // Mapear las órdenes a DTOs y devolver la respuesta
        return ResponseEntity.ok(ApiResponse.success("success", orders.stream().map(OrderDTO::new).collect(Collectors.toList())));
    }

    @GetMapping("/orders/current")
    public ResponseEntity<ApiResponse> getMyOrders() {
        Object anyAuthenticated;

        try {
            // Obtener el usuario autenticado y el tipo de cuenta (Business, Delivery, Admin)
            anyAuthenticated = authController.getAnyAuthenticated();
        } catch (Exception e) {
            // Si ocurre un error al obtener la cuenta autenticada, se maneja aquí
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }

        if (anyAuthenticated == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Account is not authenticated"));
        }

        List<Order> orders = new ArrayList<>();

        // Lógica basada en el tipo de cuenta del usuario autenticado
        if (anyAuthenticated instanceof Business) {
            Business business = (Business) anyAuthenticated;
            orders = ordersControlService.getOrdersService().getOrdersByBusinessId(business.getId());
        } else if (anyAuthenticated instanceof Delivery) {
            Delivery delivery = (Delivery) anyAuthenticated;
            orders = ordersControlService.getOrdersService().getOrdersByDeliveryId(delivery.getId());
        } else if (anyAuthenticated instanceof Admin) {
            Admin admin = (Admin) anyAuthenticated;
            orders = ordersControlService.getAllOrders();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("User role not authorized"));
        }

        // Mapear las órdenes a DTOs y devolver la respuesta
        return ResponseEntity.ok(ApiResponse.success("success", orders.stream().map(OrderDTO::new).collect(Collectors.toList())));
    }

    @PostMapping("/orders/newOrder")
    public ResponseEntity<ApiResponse> newOrder(@RequestBody NewOrderRequest newOrder) {
        Business business;

        try {
            // Obtener el Business autenticado
            business = authController.getAuthenticatedBusiness();
        } catch (Exception e) {
            // Si el Business no está autenticado o hay un error al obtenerlo
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }

        // Verificar si el Business está activo
        if (business.getAccountStatus().equals(Business.AccountStatus.DESACTIVADO)
                || business.getAccountStatus().equals(Business.AccountStatus.SUSPENDIDO)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Estas " + business.getAccountStatus().name() + " no puedes enviar ordenes"));
        }

        // Buscar al cliente por su número de teléfono
        Customer customer = userService.getCustomerService().findByPhoneNumber(newOrder.getCustomerPhone());

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Cliente no encontrado"));
        }

        // Crear la nueva orden
        OrderType orderType = newOrder.getOrderType();
        Order order = new Order(business, customer, newOrder);
        order.setOrderType(orderType);

        if (newOrder.getQuoteDTO() != null) {
            order.setQuoteDTO(newOrder.getQuoteDTO());
        }

        // Agregar la orden al sistema
        ordersControlService.addOrder(order);

        // Retornar respuesta con la orden creada
        return ResponseEntity.ok(ApiResponse.success("Orden creada exitosamente", new OrderDTO(order)));
    }

    @PostMapping("/orders/newTestOrder")
    public ResponseEntity<ApiResponse> newTestOrder(@RequestBody NewTestOrderRequest newOrder) {
        Admin admin;

        try {
            // Obtener el Business autenticado
            admin = authController.getAuthenticatedAdmin();
        } catch (Exception e) {
            // Si el Business no está autenticado o hay un error al obtenerlo
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }

        Business business = userService.getBusinessService().getById(newOrder.getBusinessId());

        if (business == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Negocio no encontrado"));
        }

        // Buscar al cliente por su número de teléfono
        Customer customer = userService.getCustomerService().findByPhoneNumber(newOrder.getCustomerPhone());

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Cliente no encontrado"));
        }

        // Crear la nueva orden
        OrderType orderType = newOrder.getOrderType();
        Order order = new Order(business, customer, newOrder);

        order.setOrderType(orderType);

        if (newOrder.getQuoteDTO() != null) {
            order.setQuoteDTO(newOrder.getQuoteDTO());
        }

        // Agregar la orden al sistema
        ordersControlService.addOrder(order);

        // Retornar respuesta con la orden creada
        return ResponseEntity.ok(ApiResponse.success("Orden de prueba creada exitosamente", new OrderDTO(order)));
    }

    @PostMapping("/orders/setDelivery")
    public ResponseEntity<ApiResponse> setDelivery(@RequestBody AdminOrderSetDelivery aosd) {
        Admin admin;

        try {
            // Obtener el Admin autenticado
            admin = authController.getAuthenticatedAdmin();
        } catch (Exception e) {
            // Si el Admin no está autenticado o hay un error al obtenerlo
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }

        aosd.setRequester(admin);

        // Llamar al servicio para configurar la entrega
        ApiResponse result = ordersControlService.orderSetDelivery(aosd);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/orders/takeOrRejectOrder")
    public ResponseEntity<ApiResponse> takeOrRejectOrder(@RequestBody DeliveryTakeOrRejectOrder dtoro) {
        Delivery delivery;

        try {
            // Obtener el Delivery autenticado
            delivery = authController.getAuthenticatedDelivery();
            dtoro.setRequester(delivery);

            // Llamar al servicio para tomar o rechazar la orden
            ApiResponse result = ordersControlService.getOrdersService().takerOrRejectOrderByDelivery(delivery, dtoro);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Si el Delivery no está autenticado o hay un error al obtenerlo
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

}
