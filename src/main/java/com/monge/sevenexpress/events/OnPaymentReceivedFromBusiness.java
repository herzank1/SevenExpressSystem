/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.monge.sevenexpress.events;

import com.monge.sevenexpress.entities.Business;
import java.time.Clock;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OnPaymentReceivedFromBusiness extends ApplicationEvent {

    private final Business business;

    public OnPaymentReceivedFromBusiness(Object source, Business business) {
        super(source);
        this.business = business;
    }

}
