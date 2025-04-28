/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, String> {

    Optional<Delivery> findById(String id);
     List<Delivery> findByAccountStatus(UserProfile.AccountStatus status);
    
    long count();


}
