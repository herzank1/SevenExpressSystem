/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.monge.sevenexpress.utils.StringListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
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
        this.balanceAccount = new BalanceAccount();
        this.businessContract = BusinessContract.generate_perOrderService_contract();

    }

    
    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id") // Clave foránea
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private BalanceAccount balanceAccount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "business_contract_id", referencedColumnName = "id") // Clave foránea
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private BusinessContract businessContract;

   public boolean exceedsItsDebt() {
    if (this.businessContract == null || this.balanceAccount == null) {
        throw new IllegalStateException("BusinessContract o BalanceAccount no pueden ser null");
    }

    double maximumDebt = -Math.abs(this.businessContract.getMaximumDebt()); // Asegura que sea negativo
    double balance = this.balanceAccount.getBalance();

    return balance < maximumDebt;
}

    
    public enum AccountStatus{
    //0,1,2
         ACTIVADO,DESACTIVADO, SUSPENDIDO;
    
    }



}
