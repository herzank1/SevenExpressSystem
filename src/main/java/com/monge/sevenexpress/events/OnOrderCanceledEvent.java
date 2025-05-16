/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.events;

import com.monge.sevenexpress.entities.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OnOrderCanceledEvent extends ApplicationEvent{
    
    private final Order order;
    private final String canceler;
    private final String reason;

    public OnOrderCanceledEvent(Object source, Order order, String canceler, String reason) {
        super(source);
        this.order = order;
        this.canceler = canceler;
        this.reason = reason;
    }
    
     
    
    
}
