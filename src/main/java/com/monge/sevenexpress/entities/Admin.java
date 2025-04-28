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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Diego Villarreal
 * Esta clase representa la cuenta de un Adminitrador
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "admins")
public class Admin implements UserProfile {
    
    @Id
    private String id;
    
    private String name;
    private String address;
    private String phoneNumber;
    
    @Convert(converter = StringListConverter.class)
    private ArrayList<String> tags;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "balance_account_id", referencedColumnName = "id")
    private BalanceAccount balanceAccount = new BalanceAccount(); // inicializado directo

   private AccountStatus accountStatus = AccountStatus.DESACTIVADO;
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPhone() {
        return phoneNumber; // ahora implementado correctamente
    }
    
    @Override
    public Role getType() {
        return Role.ADMIN;
    }
    
    @Override
    public AccountStatus getStatus() {
        return accountStatus;
    }
    
    @Override
    public String getAddress() {
        return address;
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
        this.accountStatus =status;
    }
    
}
