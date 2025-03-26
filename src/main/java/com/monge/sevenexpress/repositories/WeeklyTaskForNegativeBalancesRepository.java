/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.WeeklyTaskForNegativeBalances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface  WeeklyTaskForNegativeBalancesRepository  extends JpaRepository<WeeklyTaskForNegativeBalances, Long> {
    WeeklyTaskForNegativeBalances findTopByOrderByExecutionDateDesc();

    
}
