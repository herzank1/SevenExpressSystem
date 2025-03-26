/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    @Transient
    private String position;//GPS
    @Transient
    private boolean conected;
    /*variable remporal para el acceso por userName al map de deliveries*/
    @Transient
    private String userName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id") // Clave foránea
    private BalanceAccount balanceAccount;
    
    /*variable para registra la ultima vez que se le asigno una orden y hacer un sort en el array*/
    @Transient
    long lastOrderAsignedTimeStamp;
    
    private Business.AccountStatus accountStatus;

    public Delivery() {
        this.accountStatus = Business.AccountStatus.ACTIVADO;
    }
    
    

}
