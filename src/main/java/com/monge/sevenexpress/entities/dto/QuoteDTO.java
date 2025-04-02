/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.subservices.GoogleMapsService;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class QuoteDTO {

    private String from;
    private String to;
    /*opcional*/
    private String fromPosition;
    private String toPosition;

    /*use google api to fill*/
    private double kilometers;
    private double minutes;

    private float cost;

    
    public void fill(GoogleMapsService.DistanceDetails distanceDetails){
        this.kilometers = distanceDetails.getKilometers();
        this.minutes = distanceDetails.getMinutes();
    
    }

    public void calc(BusinessContract contract) {
        /*-1 indica que google no pudo calcular, entonces regresamos por defecto 50*/
        if(kilometers==-1||minutes==-1){
            cost = 50;
            return;
        
        }

        int kmExtras = 0;

        if (kilometers > contract.getKmBase()) {
            kmExtras = (int) (kilometers-contract.getKmBase());
        }
        
        cost = contract.getKmBaseCost()+(kmExtras*contract.getKmExtraCost());

    }

}
