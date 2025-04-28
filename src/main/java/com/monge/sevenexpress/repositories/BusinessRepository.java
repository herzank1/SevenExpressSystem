/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.UserProfile.AccountStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, String> {

    Optional<Business> findById(String id);
    
    Optional<Business> findByBalanceAccountId(Long balanceAccountId);
    
     List<Business> findByAccountStatus(AccountStatus status);
    
    List<Business> findAll();

    long count();

}
