/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class ApiRequest {
    
    private String requesterId; //id del delivery,business,admin establecer en el backend
    private User.Role requesterType;//BUSINESS, CUSTOMER,DELIVERY,ADMIN establecer en el backend
    private String action;

    public ApiRequest(String action) {
        this.action = action;
    }
    
    @JsonIgnore
    public void setRequester(User requester) {
        
            this.setRequesterId(requester.getAccountId());
            this.setRequesterType(requester.getRole());
      
    }
    
    
    // Setters para Delivery y Business
    public void setRequester(Object requester) {
        if (requester instanceof Delivery) {
            this.setRequesterId(((Delivery) requester).getId());
            this.setRequesterType(User.Role.DELIVERY);
        } else if (requester instanceof Business) {
            this.setRequesterId(((Business) requester).getId());
            this.setRequesterType(User.Role.BUSINESS);
        }
    }
    
}
