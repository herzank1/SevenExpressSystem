/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.UserProfile.AccountStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findById(String id);
    List<Admin> findByAccountStatus(AccountStatus status);

    
    long count();

}
