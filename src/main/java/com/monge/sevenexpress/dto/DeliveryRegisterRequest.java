 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 * Esta clase es compatible para registra un Admin
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeliveryRegisterRequest extends ApiRequest {

    public DeliveryRegisterRequest() {
        super("register");
    }
    
    

    private String userName;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;


}
