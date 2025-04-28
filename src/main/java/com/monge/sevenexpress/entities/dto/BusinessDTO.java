/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.Business;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class BusinessDTO {
    private String id;
    private String businessName;
    private String phoneNumber;
    private String address;
    private String position;//GPS
    
    private BalanceAccountDTO balanceAccount;
    private BusinessContractDTO businessContract;

    public BusinessDTO(Business business) {
        this.id = business.getId();
        this.businessName = business.getBusinessName();
        this.phoneNumber = business.getPhoneNumber();
        this.address = business.getAddress();
        this.position = business.getPosition();
        this.balanceAccount = new BalanceAccountDTO(business.getBalanceAccount());
        
        this.businessContract = new BusinessContractDTO(business.getBusinessContract());
    }
}