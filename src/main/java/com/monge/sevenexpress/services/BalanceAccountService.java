/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.repositories.BalanceAccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class BalanceAccountService {

    @Autowired
    private final BalanceAccountRepository balanceAccountRepository;

    /***
     * Incrementa el balance y actualiza en la base de datos
     * @param balanceAccount
     * @param amount
     * @return 
     */
    public BalanceAccount sumBalance(BalanceAccount balanceAccount, Double amount) {
        balanceAccount.sum(amount);
        balanceAccountRepository.save(balanceAccount);
        return balanceAccount;
    }

    /***
     * resta balance y actualiza en la base de datos
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

    /**
     * Obtiene o crea un BalanceAccount asociado al Business.
     * todo business debe tener un balance accout asociado, asegurate de que este bañance account se
     * asigne a business
     */
    @Transactional
    public BalanceAccount getBalanceAccount(Business business) {
        BalanceAccount balanceAccount = null;
        if (business.getBalanceAccount() == null) {
            balanceAccount = new BalanceAccount();
            balanceAccount.setBalance(0.0);
            balanceAccount = save(balanceAccount);

        }
        return balanceAccount;
    }
    
    
        @Transactional
    public BalanceAccount getBalanceAccount(Delivery delivery) {
        if (delivery.getBalanceAccount() == null) {
            BalanceAccount balanceAccount = new BalanceAccount();
            balanceAccount.setBalance(0.0);
            balanceAccount = save(balanceAccount);

        }
        return delivery.getBalanceAccount();
    }

}
