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
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BusinessRegisterRequest extends ApiRequest {

    public BusinessRegisterRequest() {
        super("register");
    }

    private String userName;
    private String password;
    private String businessName;
    private String address;
    private String phoneNumber;
    private String position;

}
