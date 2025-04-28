/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.monge.sevenexpress.entities.PaymentReceipt;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class ReceiptUpdateRequest {
    private long id;
    private PaymentReceipt.PaymentStatus status;
}
