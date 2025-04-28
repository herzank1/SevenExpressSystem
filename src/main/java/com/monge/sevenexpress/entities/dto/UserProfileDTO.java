/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.UserProfile.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.monge.sevenexpress.entities.User.Role;


import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.UserProfile;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileDTO {

    private String id;
    private String name;
    private String address;
    private String phone;
    private Role type;
    private AccountStatus status;
    private BalanceAccount balanceAccount;

    public UserProfileDTO() {
    }
    
    

  // Constructor que recibe un UserProfile
    public UserProfileDTO(UserProfile userProfile) {
        this.id = userProfile.getId();
        this.name = userProfile.getName();
        this.address = userProfile.getAddress();
        this.phone = userProfile.getPhone();
        this.balanceAccount = userProfile.getBalanceAccount();
        this.status = userProfile.getStatus();
        this.type = userProfile.getType();
    }
}
