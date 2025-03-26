/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.dto.PaymentReceiptRequest;
import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.repositories.PaymentReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
public class PaymentReceiptService {

    @Autowired
    private PaymentReceiptRepository paymentReceiptRepository;
    
    public PaymentReceipt savePaymentReceipt(PaymentReceiptRequest request) {
        PaymentReceipt receipt = new PaymentReceipt();
        receipt.setBalanceAccountId(request.getBalanceAccountId());
        receipt.setAmount(request.getAmount());
        receipt.setPaymentMethod(request.getPaymentMethod());
        receipt.setStatus(request.getStatus());
        receipt.setReference(request.getReference());
        receipt.setConcept(request.getConcept());
        receipt.setBase64Image(request.getBase64Image());

        return paymentReceiptRepository.save(receipt);
    }

}
