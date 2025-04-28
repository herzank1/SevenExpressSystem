/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_receipts")
public class PaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balanceAccountId;

    private double amount;

    private String reference;
    
    private String concept;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String imageUrl;

    public enum PaymentMethod {
        SPEI, OXXO, OTRO
    }

    public enum PaymentStatus {
        PROCESSED, REJECTED, PENDING
    }
}
