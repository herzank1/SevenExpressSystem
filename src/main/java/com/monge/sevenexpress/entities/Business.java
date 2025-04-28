/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.monge.sevenexpress.entities.User.Role;

import com.monge.sevenexpress.utils.StringListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Diego Villarreal
 * Esta clase representa la cuenta de un Negocio/Restaurante
 */

@Data
@NoArgsConstructor
@Entity
@Table(name = "businesess")
public class Business implements UserProfile {

    @Id
    private String id;

    private String businessName;
    private String address;
    private String phoneNumber;
    private String position; // GPS

    private AccountStatus accountStatus = AccountStatus.DESACTIVADO;

    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private BalanceAccount balanceAccount = new BalanceAccount();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "business_contract_id", referencedColumnName = "id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private BusinessContract businessContract = BusinessContract.generate_perOrderService_contract();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return businessName;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPhone() {
        return phoneNumber;
    }

    @Override
    public Role getType() {
        return Role.BUSINESS;
    }

    @Override
    public AccountStatus getStatus() {
        return accountStatus;
    }

    @Override
    public BalanceAccount getBalanceAccount() {
        return balanceAccount;
    }

    public boolean exceedsItsDebt() {
        if (this.businessContract == null || this.balanceAccount == null) {
            throw new IllegalStateException("BusinessContract o BalanceAccount no pueden ser null");
        }

        double maximumDebt = -Math.abs(this.businessContract.getMaximumDebt()); // Asegura que sea negativo
        double balance = this.balanceAccount.getBalance();

        return balance < maximumDebt;
    }

    @Override
    public void setName(String name) {
        this.businessName = name;
    }

    @Override
    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    @Override
    public void setStatus(AccountStatus status) {
        this.accountStatus = status;
    }
}
