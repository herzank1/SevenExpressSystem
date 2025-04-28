/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer,String>{
    
   
     Optional<Customer> findByPhoneNumber(String phoneNumber);
     List<Customer> findByAccountStatus(UserProfile.AccountStatus status);
     
 
     long count();
    
}
