/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;


/**
 *
 * @author DeliveryExpress
 */
@Entity
@Data
public class WeeklyTaskForNegativeBalances {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime executionDate;
    
    private double totalBusinesess;
    private double totalNegative;

    public WeeklyTaskForNegativeBalances(LocalDateTime now) {
   this.executionDate = now;
    }

}
