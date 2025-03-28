/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.Customer;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class CustomerDTO {


    private String name;
    private String address;
    private String phoneNumber;
    private String position;//GPS

    public CustomerDTO(Customer customer) {
        this.name = customer.getName();
        this.address = customer.getAddress();
        this.phoneNumber = customer.getPhoneNumber();
        this.position = customer.getPosition();
    }

    
   
}
