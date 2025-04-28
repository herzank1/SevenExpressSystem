/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.entities.User.Role;

/**
 *
 * @author DeliveryExpress
 */
public interface UserProfile {

    String getId();
    void setId(String id);

    String getName();
    void setName(String name);

    String getAddress();
    void setAddress(String address);

    String getPhone();
    void setPhone(String phone);

    Role getType(); 
 

    AccountStatus getStatus();
    void setStatus(AccountStatus status);

    BalanceAccount getBalanceAccount();
    void setBalanceAccount(BalanceAccount balanceAccount);

    enum AccountStatus {
        ACTIVADO, DESACTIVADO, SUSPENDIDO
    }
}

