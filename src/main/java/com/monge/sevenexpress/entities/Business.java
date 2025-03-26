/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Entity
@Data
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String businessName;
    private String address;
    private String phoneNumber;
    private String position;//GPS
    
    private AccountStatus accountStatus;

    public Business() {
        this.accountStatus = AccountStatus.DESACTIVADO;

    }
    
    
    
     @Embedded
    private Tags tags = new Tags();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id") // Clave foránea
    @JsonManagedReference // Se serializa y maneja como la "parte principal" de la relación
    private BalanceAccount balanceAccount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "business_contract_id", referencedColumnName = "id") // Clave foránea
    @JsonManagedReference // Se serializa y maneja como la "parte principal" de la relación
  
    private BusinessContract businessContract;
    
    
    public enum AccountStatus{
    //0,1,2
         ACTIVADO,DESACTIVADO, SUSPENDIDO;
    
    }



}
