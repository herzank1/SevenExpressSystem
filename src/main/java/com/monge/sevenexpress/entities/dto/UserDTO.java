/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.entities.User.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String accountId;
    private Role role;
    private boolean active;

    public UserDTO() {
    }
    
    
    
     public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.accountId = user.getAccountId();
        this.role = user.getRole();
        this.active = user.isActive();
    }
}
