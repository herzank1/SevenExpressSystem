/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;

import com.monge.sevenexpress.enums.OrderStatus;
import com.monge.sevenexpress.utils.LoggerUtils;
import com.monge.sevenexpress.utils.Position;
import java.util.ArrayList;
import java.util.Comparator;
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
public class OrderAsignatorService {

    private final OrdersService ordersService;
    private final DeliveryService deliveryService;
    private final GoogleMapsService googleMapsService;

    private final int MAX_ORDER_PER_DELIVERY = 2;
    private final double MAX_DISTANCE_FOR_PICKUP = 6;

    @Autowired
    public OrderAsignatorService(OrdersService ordersService, DeliveryService deliveryService,
            GoogleMapsService googleMapsService) {
        this.ordersService = ordersService;
        this.deliveryService = deliveryService;
        this.googleMapsService = googleMapsService;

    }

    @Scheduled(fixedRate = 15000) // Se ejecuta cada 15 segundos
    private void startAtmAsignator() {

        /*obtenemos la lista de ordenes disponibles (sin repartidor)*/
        ArrayList<Order> availables = ordersService
                .getAllOrders()
                .stream()
                .filter(c -> c.getStatus() == OrderStatus.PREPARANDO || c.getStatus() == OrderStatus.LISTO)
                .filter(c -> c.getDelivery() == null)
                .collect(Collectors.toCollection(ArrayList::new));

        if (availables.isEmpty()) {
            return;
        }

        /*obtenemos la lista de repartidores conectados*/
        ArrayList<Delivery> conectedDeliveries = deliveryService.getConectedDeliveries();


        /*sorteamos a los repartitores al mas perreado al recien ejecutado, (pedidos recientes)*/
        conectedDeliveries = sortDeliveriesByLastOrderReceived(conectedDeliveries);

        if (conectedDeliveries.isEmpty()) {
            return;
        }

        for (Delivery delivery : conectedDeliveries) {
            LoggerUtils.printInfo("buscado orden para el repartidor " + delivery.getId());
            int orderCount = ordersService.getDeliveryOrderCount(delivery);
            /*si el repartidor tiene capacidad o disponible*/
            if (orderCount < MAX_ORDER_PER_DELIVERY) {
                for (Order order : availables) {
                    LoggerUtils.printInfo("revisando orden " + order.getId().toString());
                    /*si esta cercas*/
                    if (calcDistanceKm(order, delivery) <= MAX_DISTANCE_FOR_PICKUP) {
                        order.getAsignationCountDown().assign(delivery, true);

                        LoggerUtils.printInfo("Orden " + order.getId().toString() + " asignada al repartidor " + delivery.getId());
                        break;
                    }
                }

            }
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
            String businessPosition=null;
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

}
