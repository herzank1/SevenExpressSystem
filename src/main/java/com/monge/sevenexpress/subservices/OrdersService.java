/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.events.OrderDeliveredEvent;
import com.monge.sevenexpress.dto.AdminOrderSetDelivery;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest.UserIndication;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.enums.OrderStatus;
import static com.monge.sevenexpress.enums.OrderStatus.*;
import static com.monge.sevenexpress.enums.OrderStatus.LISTO;
import com.monge.sevenexpress.repositories.OrderRepository;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.utils.OrderLogManager;

/**
 *
 * @author DeliveryExpress
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Data
@Service
public class OrdersService {
    
    @Autowired
    private final ApplicationEventPublisher applicationEventPublisher;

    private final List<Order> currentOrders = Collections.synchronizedList(new CopyOnWriteArrayList<>());

    @Autowired
    private OrderRepository orderRepository;


    public void addOrder(Order order) {
        if (order.getBusiness() == null) {
            throw new IllegalArgumentException("La orden debe tener un negocio asignado.");
        }
        currentOrders.add(order);
    }

    // Función para eliminar una orden por su ID
    public void removeOrderById(UUID orderId) {
        currentOrders.removeIf(order -> order.getId().equals(orderId));
    }

    // Función para obtener una orden por su ID
    public Order getOrderById(UUID orderId) {
        return currentOrders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null); // Retorna null si no se encuentra la orden
    }

    // Función para obtener las órdenes de un negocio por su ID
    public List<Order> getOrdersByBusinessId(long businessId) {
        return (ArrayList<Order>) currentOrders.stream()
                .filter(order -> order.getBusiness() != null && order.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }

    // Función para obtener las órdenes de un cliente por su ID
    public List<Order> getOrdersByCustomerId(long customerId) {
        return (ArrayList<Order>) currentOrders.stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
    }

    // Función para obtener las órdenes de un delivery por su ID!
    public List<Order> getOrdersByDeliveryId(long deliveryId) {
        return (ArrayList<Order>) currentOrders.stream()
                .filter(order -> order.getDelivery() != null && order.getDelivery().getId().equals(deliveryId))
                .collect(Collectors.toList());
    }

    // Función para obtener todas las órdenes
    public List<Order> getAllOrders() {
        return currentOrders;
    }
    
    
    public List<Order> getInProcessOrders() {
    return currentOrders.stream()
            .filter(order -> order.getStatus() != OrderStatus.ENTREGADO && order.getStatus() != OrderStatus.CANCELADO)
            .collect(Collectors.toList());
}

    public int getDeliveryOrderCount(Delivery delivery) {
        if (delivery == null) {
            return 0; // Si el delivery es null, no tiene sentido buscar órdenes para él
        }

        return (int) currentOrders.stream()
                .filter(c -> c.getDelivery() != null && c.getDelivery().equals(delivery))
                .count();
    }

    public ApiResponse changeOrderStatus(ChangeOrderStatusRequest cosr) {

        // Validar existencia de la orden
        Order order = getOrderById(cosr.getOrderId());
        if (order == null) {
            return ApiResponse.error("Esta orden no existe!");
        }

        // Validar autorización
        boolean isRequesterAdmin = cosr.getRequesterType().equals(User.Role.ADMIN);
        boolean isRequesterBusiness = order.getBusiness().getId().equals(cosr.getRequesterId());
        boolean isRequesterDelivery = order.getDelivery() != null && order.getDelivery().getId().equals(cosr.getRequesterId());

        if (!(isRequesterAdmin || isRequesterBusiness || isRequesterDelivery)) {
            return ApiResponse.error("No estás autorizado para modificar esta orden!");
        }

        /*verifica si cosr es un cambio de estado*/
        if (cosr.getNewStatus() != null) {
            // Validar cambio de estado
            if (!canChangeStatus(cosr.getRequesterType(), order.getStatus(), cosr.getNewStatus())) {
                return ApiResponse.error("No se puede cambiar el estado de " + order.getStatus() + " a " + cosr.getNewStatus());
            }

            // Actualizar estado
            order.setStatus(cosr.getNewStatus());

            /*post cambio de estado*/
            postChangeStatus(order);

        }

        /*verifica si cosr es una indicacion*/
        if (cosr.getIndication() != null) {
            /*executar indication*/
            executeUserIndication(order, cosr.getIndication());
        }

        /*agregamos el registro a la orden*/
   
          OrderLogManager.addLog(order.getOrderLog(), cosr);

        return ApiResponse.success("Se ha actualizado el estado de esta orden!", order);
    }

    /*valida si el role del usuario puede modificar a ese estado*/
    private boolean canChangeStatus(User.Role role, OrderStatus current, OrderStatus newStatus) {
        if (!isValidTransition(current, newStatus)) {
            return false; // No permitir cambios a estados anteriores, excepto CANCELADO
        }

        switch (newStatus) {
            case LISTO:
                return role == User.Role.BUSINESS;

            case EN_CAMINO:
            case EN_DOMICILIO:
            case ENTREGADO:
                return role == User.Role.DELIVERY;

            case CANCELADO:
                return true; // Cualquier usuario puede cancelar en cualquier momento

            default:
                return false;
        }
    }

    /*valida si el estado nuevo es valido*/
    private boolean isValidTransition(OrderStatus current, OrderStatus newStatus) {
        return newStatus == OrderStatus.CANCELADO || newStatus.ordinal() > current.ordinal();
    }

    private boolean executeUserIndication(Order order, UserIndication userIndication) {

        switch (userIndication) {
            case NONE:
                return true;

            case ARRIVED_TO_BUSINESS:
                order.setArrivedToBusiness(true);
 
            default:
                return true;
        }

    }

    /*despues de que haya cambiado a un estado*/
    private boolean postChangeStatus(Order order) {
        switch (order.getStatus()) {
            case LISTO:
                return true;

            case EN_CAMINO:
            //case EN_DOMICILIO:
            case ENTREGADO:
                /*cargar servicio a negocio o pagar al delivery*/
                executeContractPostOrderDelivered(order);
                orderRepository.save(order);
                return true;

            case CANCELADO:
                orderRepository.save(order);
                return true; // Cualquier usuario puede cancelar en cualquier momento

            default:
                return false;
        }

    }
    
    public void executeContractPostOrderDelivered(Order order){
        OrderDeliveredEvent event = new OrderDeliveredEvent(this, order);
        applicationEventPublisher.publishEvent(event);
    }
    
    

    public ApiResponse takerOrRejectOrderByDelivery(DeliveryTakeOrRejectOrder dtoro) {

        // Validar existencia de la orden
        Order order = getOrderById(dtoro.getOrderId());
        if (order == null) {
            return ApiResponse.error("Esta orden no existe!");
        }

        /*verificar repartidor*/
        if (order.getDelivery() != null && order.getDelivery().getId().equals(dtoro.getRequesterId())) {

            OrderLogManager.addLog(order.getOrderLog(), dtoro);
           
            if (dtoro.isTake()) {
                order.getAsignationCountDown().take();
                return ApiResponse.success("Repartidor a tomado la orden", order);
            } else {

                order.getAsignationCountDown().reject();
                return ApiResponse.success("Repartidor a rechazo la orden", order);
            }

        }

        return ApiResponse.success("No se pudo tomar esta orden.", null);
    }

    /***
     * Asigna orden a un repartido de forma manual (admins)
     * @param aosd
     * @return 
     */
    public ApiResponse orderSetDelivery(AdminOrderSetDelivery aosd) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
