/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.monge.sevenexpress.entities.BusinessContract;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface BusinessContractRepository extends JpaRepository<BusinessContract,Long>{

    
}
