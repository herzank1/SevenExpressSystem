/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.events.OnOrderDeliveredEvent;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private final ConcurrentMap<UUID, Order> currentOrders = new ConcurrentHashMap<>();

    @Autowired
    private OrderRepository orderRepository;

    // Métodos de acceso
    public void addOrder(Order order) {
        if (order.getBusiness() == null) {
            throw new IllegalArgumentException("La orden debe tener un negocio asignado.");
        }
        currentOrders.put(order.getId(), order);
    }

    // Función para eliminar una orden por su ID
    public void removeOrderById(UUID orderId) {
        currentOrders.remove(orderId); // Operación atómica
    }

    public Order getOrderById(UUID orderId) {
        return currentOrders.get(orderId); // Operación atómica O(1)
    }

    // Función para obtener las órdenes de un negocio por su ID
    public List<Order> getOrdersByBusinessId(long businessId) {
        return new ArrayList<>(currentOrders.values().stream()
                .filter(order -> order.getBusiness() != null && order.getBusiness().getId().equals(businessId))
                //.filter(order -> !order.getStatus().equals(OrderStatus.ENTREGADO) && !order.getStatus().equals(OrderStatus.CANCELADO))
                .collect(Collectors.toList()));
    }

// Función para obtener las órdenes de un cliente por su ID
    public List<Order> getOrdersByCustomerId(long customerId) {
        return new ArrayList<>(currentOrders.values().stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList()));
    }

// Función para obtener las órdenes de un delivery por su ID
    public List<Order> getOrdersByDeliveryId(long deliveryId) {
        return new ArrayList<>(currentOrders.values().stream()
                .filter(order -> order.getDelivery() != null && order.getDelivery().getId().equals(deliveryId))
                .collect(Collectors.toList()));
    }

// Función para obtener todas las órdenes
    public List<Order> getAllOrders() {
        return new ArrayList<>(currentOrders.values());
    }

    public List<Order> getInProcessOrders() {
        return currentOrders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.ENTREGADO && order.getStatus() != OrderStatus.CANCELADO)
                .collect(Collectors.toList());
    }

    public int getDeliveryOrderCount(Delivery delivery) {
        if (delivery == null) {
            return 0; // Si el delivery es null, no tiene sentido buscar órdenes para él
        }

        return (int) currentOrders.values().stream()
                .filter(c -> c.getDelivery() != null && c.getDelivery().equals(delivery))
                .count();
    }

    public ApiResponse changeOrderStatus(ChangeOrderStatusRequest cosr) {
        // Bloqueamos por ID de orden para permitir concurrencia entre diferentes órdenes
        Order order = getOrderById(cosr.getOrderId());
        if (order == null) {
            return ApiResponse.error("Esta orden no existe!");
        }

        synchronized (order) { // Bloqueamos ESTA orden específica
            // Validar autorización
            boolean isRequesterAdmin = cosr.getRequesterType().equals(User.Role.ADMIN);
            boolean isRequesterBusiness = order.getBusiness().getId().equals(cosr.getRequesterId());
            boolean isRequesterDelivery = order.getDelivery() != null && order.getDelivery().getId().equals(cosr.getRequesterId());

            if (!(isRequesterAdmin || isRequesterBusiness || isRequesterDelivery)) {
                return ApiResponse.error("No estás autorizado para modificar esta orden!");
            }

            if (cosr.getNewStatus() != null) {
                if (!canChangeStatus(cosr.getRequesterType(), order.getStatus(), cosr.getNewStatus())) {
                    return ApiResponse.error("No se puede cambiar el estado de " + order.getStatus() + " a " + cosr.getNewStatus());
                }

                order.setStatus(cosr.getNewStatus());
                postChangeStatus(order);
            }

            if (cosr.getIndication() != null) {
                executeUserIndication(order, cosr.getIndication());
            }

            OrderLogManager.addLog(order.getOrderLog(), cosr);
        }

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

    private boolean postChangeStatus(Order order) {
        synchronized (order) {  // Bloqueamos la orden específica
            switch (order.getStatus()) {
                case LISTO:
                    return true;

                case EN_CAMINO:
                case ENTREGADO:
                    executePostOrderDelivered(order);
                    // Mover persistencia y remoción fuera del bloque sincronizado
                    // para reducir el tiempo de bloqueo
                    persistAndRemoveOrder(order);
                    return true;

                case CANCELADO:
                    persistAndRemoveOrder(order);
                    return true;

                default:
                    return false;
            }
        }
    }

    // Método auxiliar para operaciones de persistencia
    private void persistAndRemoveOrder(Order order) {
        // Operaciones de persistencia fuera del bloqueo principal
        orderRepository.save(order);

        // Bloqueamos brevemente solo para la remoción
        synchronized (this) {
            removeOrderById(order.getId());
        }
    }

    public void executePostOrderDelivered(Order order) {
        OnOrderDeliveredEvent event = new OnOrderDeliveredEvent(this, order);
        applicationEventPublisher.publishEvent(event);
    }

public ApiResponse takerOrRejectOrderByDelivery(DeliveryTakeOrRejectOrder dtoro) {
    Order order = getOrderById(dtoro.getOrderId());
    if (order == null) {
        return ApiResponse.error("Esta orden no existe!");
    }

    synchronized(order) {  // Bloqueamos esta orden específica
        /*verificar repartidor*/
        if (order.getDelivery() != null && order.getDelivery().getId().equals(dtoro.getRequesterId())) {

            OrderLogManager.addLog(order.getOrderLog(), dtoro);

            if (dtoro.isTake()) {
                order.getAsignationCountDown().take();
                return ApiResponse.success("Repartidor ha tomado la orden", order);
            } else {
                order.getAsignationCountDown().reject();
                return ApiResponse.success("Repartidor ha rechazado la orden", order);
            }
        }
    }

    return ApiResponse.error("No se pudo tomar esta orden.");
}

   

}
