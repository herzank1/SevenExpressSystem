/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.repositories.BalanceAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class BalanceAccountService {

    @Autowired
    private final BalanceAccountRepository balanceAccountRepository;

    public BalanceAccount findById(long id) {
        return balanceAccountRepository.findById(id).orElse(null);
    }

    /**
     * *
     * Incrementa el balance y actualiza en la base de datos
     *
     * @param balanceAccount
     * @param amount
     * @return
     */
    public BalanceAccount sumBalance(BalanceAccount balanceAccount, Double amount) {
        balanceAccount.sum(amount);
        balanceAccountRepository.save(balanceAccount);
        return balanceAccount;
    }

    /**
     * *
     * resta balance y actualiza en la base de datos
     *
     * @param balanceAccount
     * @param amount
     * @return
     */
    public BalanceAccount subBalance(BalanceAccount balanceAccount, Double amount) {
        balanceAccount.sub(amount);
        balanceAccountRepository.save(balanceAccount);
        return balanceAccount;
    }

    public BalanceAccount save(BalanceAccount balanceAccount) {
        return balanceAccountRepository.save(balanceAccount);
    }

}
