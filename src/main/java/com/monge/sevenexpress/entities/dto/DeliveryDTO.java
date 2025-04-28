/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

/**
 *
 * @author DeliveryExpress
 */
import com.monge.sevenexpress.entities.Delivery;
import lombok.Data;
import java.util.ArrayList;

@Data
public class DeliveryDTO {

    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String position; // GPS
    private boolean conected;
    private String userName;
    private BalanceAccountDTO balanceAccount;

    public DeliveryDTO(Delivery delivery) {
        this.id = delivery.getId();
        this.name = delivery.getName();
        this.address = delivery.getAddress();
        this.phoneNumber = delivery.getPhoneNumber();
        this.position = delivery.getPosition();
        this.conected = delivery.isConected();
        if (delivery.getUserName() != null) {
            this.userName = delivery.getUserName();
        }

        this.balanceAccount = new BalanceAccountDTO(delivery.getBalanceAccount());

    }
}
