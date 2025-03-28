/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;


import com.monge.sevenexpress.intefaces.AbstractTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface  TaskRepository  extends JpaRepository<AbstractTask, Long> {
    AbstractTask findTopByOrderByExecutionDateDesc();

    
}
