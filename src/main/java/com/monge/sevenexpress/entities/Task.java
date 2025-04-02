
package com.monge.sevenexpress.entities;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


import com.monge.sevenexpress.utils.HashMapConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Va en la clase base
//@DiscriminatorColumn(name = "task_reason", discriminatorType = DiscriminatorType.STRING)  // Va en la clase base
@Entity
public  class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime executionDate;
    
   // @Column(name = "task_reason") // Asegúrate de que el nombre de la columna coincida en la base de datos
    private TaskReason taskReason;
    private ExecutionMode executionMode;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> data = new HashMap<>();

    public Task() {
    }
    
    

    public Task(LocalDateTime now) {
    this.executionDate = now;
    }

    //periodo de execucion
    public enum ExecutionMode {

        WEEKLY, //cada viernes a las 11:59pm
        
    }

    public enum TaskReason {

        CUOTA_CHARGIN,
        DISABLE_NEGATIVE_BALANCES,
        CLEAR_DB_CACHE;

    }
}
