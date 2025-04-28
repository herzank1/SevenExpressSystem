/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.entities.PaymentReceipt.PaymentMethod;
import com.monge.sevenexpress.entities.PaymentReceipt.PaymentStatus;
import lombok.Data;

@Data
public class PaymentReceiptDTO {

    private Long id;
    private Long balanceAccountId;
    private double amount;
    private String reference;
    private String concept;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String imageUrl;

    public PaymentReceiptDTO() {
    }
    
    

    public PaymentReceiptDTO(PaymentReceipt receipt) {
        this.id = receipt.getId();
        this.balanceAccountId = receipt.getBalanceAccountId();
        this.amount = receipt.getAmount();
        this.reference = receipt.getReference();
        this.concept = receipt.getConcept();
        this.paymentMethod = receipt.getPaymentMethod();
        this.status = receipt.getStatus();
        this.imageUrl = receipt.getImageUrl();
    }
}
