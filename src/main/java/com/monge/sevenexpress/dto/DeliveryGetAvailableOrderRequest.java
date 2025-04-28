/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class DeliveryGetAvailableOrderRequest {
    /*position*/
    String position;
    /*radio en kilometros*/
    int maxRatioKm;
    /*variable para mostrar los ya rechazados*/
    boolean showRejected;
    

    public DeliveryGetAvailableOrderRequest() {
        this.maxRatioKm = 100;
        this.showRejected = false;
    }
    
    
    
}
