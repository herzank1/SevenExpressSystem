/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.events;

import com.monge.sevenexpress.entities.Order;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author DeliveryExpress
 */
public class OrderDeliveredEvent extends ApplicationEvent{
    
    private final Order order;
    
      public OrderDeliveredEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
    
}
