/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.Transaction.TransactionType;
import com.monge.sevenexpress.repositories.TransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

/***
 * crea una transaccion y guarda en la BD
 * @param balanceAccountId
 * @param amount
 * @param description
 * @param type
 * @return 
 */
    public Transaction createTransaction(Long balanceAccountId, Double amount, String description, TransactionType type) {
       
        Transaction transaction = new Transaction();
        transaction.setBalanceAccountId(balanceAccountId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setType(type);

        transactionRepository.save(transaction);

        return transaction;
    }

    public List<Transaction> getTransactions(Long balanceAccountId) {
        return transactionRepository.findByBalanceAccountId(balanceAccountId);
    }
    
     public List<Transaction> getLast10Transactions(Long balanceAccountId) {
        Pageable pageable = PageRequest.of(0, 10);
        return transactionRepository.findLast10Transactions(balanceAccountId, pageable);
    }
}
