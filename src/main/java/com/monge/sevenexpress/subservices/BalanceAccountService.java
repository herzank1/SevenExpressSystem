/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.UserProfile;
import com.monge.sevenexpress.repositories.BalanceAccountRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class BalanceAccountService implements ServiceCacheable<BalanceAccount, Long> {

    private final Map<Long, BalanceAccount> balanceAccountCache = new ConcurrentHashMap<>();

    @Autowired
    private final BalanceAccountRepository balanceAccountRepository;

    /**
     * *
     * esta funcion syncroniza/actualiza el objeto BalanceAccount de userprofile
     * con el cache o la base de datos de Balance Account
     *
     * @param profile
     * @return
     */
    public UserProfile syncBalanceAccount(UserProfile profile) {
        try {

            BalanceAccount syncBalanceAccount = getById(profile.getBalanceAccount().getId());
            profile.setBalanceAccount(syncBalanceAccount);
            return profile;
        } catch (Exception e) {
            return profile;
        }

    }

    public BalanceAccount getById(long id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        BalanceAccount balanceAccount = findById(id);
        if (balanceAccount != null) {
            cacheEntity(balanceAccount.getId(), balanceAccount);
        }
        return balanceAccount;
    }

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

        try {

            BalanceAccount savedBalanceAccount = balanceAccountRepository.save(balanceAccount);
            if (savedBalanceAccount != null) {
                cacheEntity(savedBalanceAccount.getId(), savedBalanceAccount);
            }
            return savedBalanceAccount;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map<Long, BalanceAccount> getCache() {
        return balanceAccountCache;
    }

}
