/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.BalanceAccount;
import lombok.Data;

@Data
public class BalanceAccountDTO {
    private Long id;
    private double balance;

    public BalanceAccountDTO(BalanceAccount balanceAccount) {
        this.id = balanceAccount.getId();
        this.balance = balanceAccount.getBalance();
    }
}
