/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.controllers;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import com.monge.sevenexpress.dto.DeliveryUpdateLocationRequest;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.services.OrdersControlService;
import com.monge.sevenexpress.subservices.OrdersService;
import com.monge.sevenexpress.services.UserService;
import com.monge.sevenexpress.services.UtilitiesService;
import com.monge.sevenexpress.utils.LoggerUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@RequestMapping("/api/deliveries") // Prefijo para todas las rutas
public class DeliveryController {

    @Autowired
    private UserService userService;

   @Autowired
    private OrdersControlService ordersControlService;
    
    @Autowired
    private UtilitiesService UtilitiesService;
    
    @Autowired
    private ContabilityService contabilityService;

    @GetMapping("/myorders")
    public ResponseEntity<ApiResponse> getMyOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());

        if (delivery == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Business not found"));
        }

        List<Order> ordersByDeliveryUsername = ordersControlService.getOrdersService().getOrdersByDeliveryId(delivery.getId());
       // LoggerUtils.printAsJson(ordersByDeliveryUsername);
        return ResponseEntity.ok(ApiResponse.success("success", ordersByDeliveryUsername));
    }

    /**
     * Endpoint para obtener un Business con sus relaciones (BalanceAccount y
     * BusinessContract)
     */
    @GetMapping("/account")
    public ResponseEntity<ApiResponse> getDeliveryAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());


        return ResponseEntity.ok(ApiResponse.success("your delivery account", delivery));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Quitamos "Bearer " del token
        }

        UtilitiesService.getTokenBlacklistService().addToBlacklist(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente", null));

    }

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> changeOrderStatus(@RequestBody ChangeOrderStatusRequest cosr) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());
        cosr.setRequester(delivery);
        
        ApiResponse result = ordersControlService.getOrdersService().changeOrderStatus(cosr);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/switchConection")
    public ResponseEntity<ApiResponse> switchDeliveryConectionStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());
        delivery.setConected(!delivery.isConected());

        return ResponseEntity.ok(ApiResponse.success("aceptacion de pedidos...", delivery.isConected()));

    }

    @GetMapping("/getConectionStatus")
    public ResponseEntity<ApiResponse> getDeliveryConectionStatus() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());

        return ResponseEntity.ok(ApiResponse.success("aceptacion de pedidos...", delivery.isConected()));

    }

    @PostMapping("/updateLocation")
    public ResponseEntity<ApiResponse> updateDeliveryLocation(@RequestBody DeliveryUpdateLocationRequest dulr) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());

        delivery.setPosition(dulr.getPosition());

        return ResponseEntity.ok(ApiResponse.success("ubicacion actualizada!", delivery.getPosition()));

    }

    @PostMapping("/takeOrRejectOrder")
    public ResponseEntity<ApiResponse> takeOrRejectORder(@RequestBody DeliveryTakeOrRejectOrder dtoro) {
        LoggerUtils.printInfo("DELIVERY.....");
        LoggerUtils.printAsJson(dtoro);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User is not authenticated"));
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());

        dtoro.setRequester(delivery);

        ApiResponse result = ordersControlService.getOrdersService().takerOrRejectOrderByDelivery(dtoro);

        LoggerUtils.printAsJson(result);
        return ResponseEntity.ok(result);
    }

}
