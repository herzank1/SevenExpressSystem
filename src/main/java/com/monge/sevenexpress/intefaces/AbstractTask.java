/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.monge.sevenexpress.intefaces;

import com.monge.sevenexpress.utils.HashMapConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Va en la clase base
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)  // Va en la clase base
@MappedSuperclass
public abstract class AbstractTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime executionDate;
    private TaskReason TaskReason;
    private ExecutionMode executionMode;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> data = new HashMap<>();

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
