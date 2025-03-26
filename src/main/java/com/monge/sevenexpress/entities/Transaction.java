/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balanceAccountId; // ID del negocio al que pertenece la transacción
    private Double amount; // Puede ser positivo o negativo
    private String description; // Descripción de la transacción
    private LocalDateTime timestamp; // Fecha y hora de la transacción

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEPOSIT, WITHDRAWAL

    public enum TransactionType {
        DEPOSIT, // Depósito (monto positivo)
        WITHDRAWAL, // Retiro (monto negativo)
        CHARGE //cargo, monto negativo
    }

}
