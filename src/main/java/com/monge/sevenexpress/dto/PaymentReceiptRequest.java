/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.monge.sevenexpress.entities.PaymentReceipt.PaymentMethod;
import com.monge.sevenexpress.entities.PaymentReceipt.PaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)

public class PaymentReceiptRequest extends ApiRequest {
    private Long balanceAccountId;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String reference;
    private String concept;
    private String base64Image;

    public PaymentReceiptRequest() {
        super("paymentReceipt");
    }
    
  
}
