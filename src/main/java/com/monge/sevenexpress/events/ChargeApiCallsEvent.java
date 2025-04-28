/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.events;

import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ChargeApiCallsEvent extends ApplicationEvent{
    
    private final ConcurrentHashMap<Long, Double> userApiUsage;

    public ChargeApiCallsEvent( Object source,ConcurrentHashMap<Long, Double> userApiUsage) {
        super(source);
        this.userApiUsage = userApiUsage;
    }
    

    
    
}
