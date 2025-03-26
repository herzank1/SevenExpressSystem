/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.monge.sevenexpress.entities.Business;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class AdminUpdateUserBusinessRequest {
    private String userName;
    private long businessId;
    private String businessName;
    private String address;
    private String phoneNumber;
    private String position;//GPS
    
    /*change de business status*/
    Business.AccountStatus status;
    
    /*tags manager*/
    private String tag;
    TagAction tagAction;
    
    
    enum TagAction{
    
        ADD,REMOVE,
    
    }
    
}
