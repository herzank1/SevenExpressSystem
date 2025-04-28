/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.entities.User.Role;
import com.monge.sevenexpress.utils.StringListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer implements UserProfile {

    @Id
    private String id;

    private String name;
    private String address;
    private String phoneNumber;
    private String position; // GPS
    private AccountStatus accountStatus = AccountStatus.DESACTIVADO;

    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id") // Clave foránea
    private BalanceAccount balanceAccount = new BalanceAccount(); // inicializado por defecto

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
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
        return Role.CUSTOMER;
    }

    @Override
    public AccountStatus getStatus() {
        return accountStatus;  // Deberías definir accountStatus si es necesario o agregarlo si aplica
    }

    @Override
    public BalanceAccount getBalanceAccount() {
        return balanceAccount;
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
