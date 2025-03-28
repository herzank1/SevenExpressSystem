/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.Transaction.TransactionType;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class TransferDTO {

    private long id;
    private long from;
    private long to;
    private double amount;
    private TransactionType type;
    private String reason;

    public TransferDTO() {
        this.from = -1;
    }
    
    

}
