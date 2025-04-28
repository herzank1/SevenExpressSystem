/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.entities.dto.PaymentReceiptDTO;
import com.monge.sevenexpress.repositories.PaymentReceiptRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Service
public class PaymentReceiptService {

    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;
    
    public PaymentReceipt savePaymentReceipt(PaymentReceiptDTO request) {
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setBalanceAccountId(request.getBalanceAccountId());
        receipt.setAmount(request.getAmount());
        receipt.setPaymentMethod(request.getPaymentMethod());
        receipt.setStatus(request.getStatus());
        receipt.setReference(request.getReference());
        receipt.setConcept(request.getConcept());
        receipt.setImageUrl(request.getImageUrl());

        return paymentReceiptRepository.save(receipt);
    }

}
