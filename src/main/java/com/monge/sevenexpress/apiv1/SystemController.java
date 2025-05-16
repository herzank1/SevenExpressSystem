/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.SystemStatusDTO;
import com.monge.sevenexpress.services.OrdersControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@RequestMapping("/api/v1")
public class SystemController {

    @Autowired
    private OrdersControlService ordersControlService;
    // Endpoint para obtener sugerencias de direcciones

    @Autowired
    AuthController authController;

    @GetMapping("/system/status")
    public ResponseEntity<ApiResponse> status() {

        long inProcessOrders = ordersControlService.getInProcessOrders().size();
        long connectedDeliveries = ordersControlService.getConectedDeliveries().size();
        
        SystemStatusDTO status = new SystemStatusDTO(inProcessOrders,connectedDeliveries);


        return ResponseEntity.ok(ApiResponse.success("Estado del sistema", status));

    }

    @PostMapping("/system/switchAtmAsignator")
    public ResponseEntity<ApiResponse> switchAtmAsignator() {

        try {
            authController.getAuthenticatedAdmin();

            ordersControlService.setAtmAsignatorEnabled(!ordersControlService.isAtmAsignatorEnabled());
            return ResponseEntity.ok(ApiResponse.success("Asignador de ordenes automatico", ordersControlService.isAtmAsignatorEnabled()));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    @GetMapping("/system/atmAsignatorStatus")
    public ResponseEntity<ApiResponse> atmAsignatorStatus() {
        try {
            authController.getAuthenticatedAdmin();
            return ResponseEntity.ok(ApiResponse.success("Asignador de ordenes automatico", ordersControlService.isAtmAsignatorEnabled()));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

}
