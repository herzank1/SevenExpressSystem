/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.utils.StringListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;

    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;
    
     @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id") // Clave foránea
    private BalanceAccount balanceAccount;

    public Admin() {
         this.balanceAccount = new BalanceAccount();
    }

   
    
 
}
