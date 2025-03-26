/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.monge.sevenexpress.enums.OrderType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NewOrderRequest extends ApiRequest {

    public NewOrderRequest() {
        super("newOrder");
    }

    private OrderType orderType;
    private String customerPhone;
    private String customerNote;
    private float orderCost;
    private float deliveryCost;
    private int preparationTime;
    
}
