/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.dto.AdminOrderSetDelivery;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;

import com.monge.sevenexpress.enums.OrderStatus;
import com.monge.sevenexpress.subservices.DeliveryService;
import com.monge.sevenexpress.subservices.GoogleMapsService;
import com.monge.sevenexpress.subservices.OrdersService;
import com.monge.sevenexpress.utils.LoggerUtils;
import com.monge.sevenexpress.utils.Position;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress Asignador de ordenes en automatico recorre las
 * ordenes pendientes de repartidor, buscando al candidato para la recoleccion
 */
@Data
@Service
public class OrdersControlService {
     private final ReentrantLock assignLock = new ReentrantLock(); // 🔒 Bloqueo para evitar colisiones


    private final OrdersService ordersService;
    private final DeliveryService deliveryService;
    private final GoogleMapsService googleMapsService;

    private final int MAX_ORDER_PER_DELIVERY = 2;
    private final double MAX_DISTANCE_FOR_PICKUP = 4;

    @Autowired
    public OrdersControlService(OrdersService ordersService, DeliveryService deliveryService,
            GoogleMapsService googleMapsService) {
        this.ordersService = ordersService;
        this.deliveryService = deliveryService;
        this.googleMapsService = googleMapsService;

    }

   @Scheduled(fixedRate = 15000) // Se ejecuta cada 15 segundos
    private void startAtmAsignator() {
        assignLock.lock(); // 🔒 Bloquear mientras se asignan pedidos
        try {
            ArrayList<Order> availables = ordersService
                    .getAllOrders()
                    .stream()
                    .filter(c -> c.getStatus() == OrderStatus.PREPARANDO || c.getStatus() == OrderStatus.LISTO)
                    .filter(c -> c.getDelivery() == null)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (availables.isEmpty()) return;

            ArrayList<Delivery> conectedDeliveries = deliveryService.getConectedDeliveries();
            conectedDeliveries = sortDeliveriesByLastOrderReceived(conectedDeliveries);

            if (conectedDeliveries.isEmpty()) return;

            for (Delivery delivery : conectedDeliveries) {
                LoggerUtils.printInfo("Buscando orden para el repartidor " + delivery.getId());
                int orderCount = ordersService.getDeliveryOrderCount(delivery);
                
                if (orderCount < MAX_ORDER_PER_DELIVERY) {
                    for (Order order : availables) {
                        LoggerUtils.printInfo("Revisando orden " + order.getId());
                        if (calcDistanceKm(order, delivery) <= MAX_DISTANCE_FOR_PICKUP) {
                            order.getAsignationCountDown().assign(delivery, true);
                            LoggerUtils.printInfo("Orden " + order.getId() + " asignada al repartidor " + delivery.getId());
                            break;
                        }
                    }
                }
            }
        } finally {
            assignLock.unlock(); // 🔓 Liberar el bloqueo después de asignar
        }
    }

    /*ordena la lista de repartidores por timestamp de la ultima vez que recibio pedido
    para hacer equidad*/
    public ArrayList<Delivery> sortDeliveriesByLastOrderReceived(ArrayList<Delivery> list) {
        return list
                .stream()
                .sorted(Comparator.comparingLong(Delivery::getLastOrderAsignedTimeStamp)) // Orden ascendente
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /*calcula la distancia usando coordenadas
    NOTA: hacer que lance una excepcion en caso de que alguno no tenga position*/
    private double calcDistanceKm(Order order, Delivery delivery) {
        try {
            String businessPosition = null;
            if (order.getBusiness().getPosition() == null) {
                businessPosition = googleMapsService.addressToPosition(order.getBusiness().getAddress());
            }

            String deliveryPosition = delivery.getPosition();
            Position A = new Position(businessPosition);
            Position B = new Position(deliveryPosition);

            return A.calculateDistance(B);
        } catch (Exception e) {
            return 5;//defualt distance
        }

    }

    public void addOrder(Order order) {
        ordersService.addOrder(order);
    }

    public ApiResponse changeOrderStatus(ChangeOrderStatusRequest cosr) {
        return ordersService.changeOrderStatus(cosr);
    }

    public ApiResponse orderSetDelivery(AdminOrderSetDelivery aosd) {
        assignLock.lock(); // 🔒 Bloqueo antes de modificar el caché
        try {
            Delivery delivery = deliveryService.getById(aosd.getDeliveryId());
            if (delivery == null) {
                return ApiResponse.error("Delivery no encontrado en caché");
            }
            
            boolean assign = ordersService.getOrderById(aosd.getOrderId())
                    .getAsignationCountDown()
                    .assign(delivery, true);

            return ApiResponse.success("Asignación manual", assign);
        } finally {
            assignLock.unlock(); // 🔓 Liberar el bloqueo después de la operación
        }
    }

    public List<Order> getAllOrders() {
        return ordersService.getAllOrders();
    }

    public List<Order> getInProcessOrders() {
        return ordersService.getInProcessOrders();
    }

    public List<Delivery> getConectedDeliveries() {
        return deliveryService.getConectedDeliveries();
    }

}
