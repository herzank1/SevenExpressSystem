/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.dto.TransferDTO;
import com.monge.sevenexpress.subservices.BalanceAccountService;
import com.monge.sevenexpress.events.OrderDeliveredEvent;
import com.monge.sevenexpress.subservices.PaymentReceiptService;
import com.monge.sevenexpress.subservices.TransactionService;
import java.util.Optional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Service
public class ContabilityService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BalanceAccountService balanceAccountService;

    @Autowired
    private PaymentReceiptService paymentReceiptService;

    @Async
    @EventListener
    public void onOrderDelivered(OrderDeliveredEvent event) {
        Order order = event.getOrder();
        // Lógica cuando la orden ha sido entregada
        System.out.println("Order delivered: " + order);
        executeContractPostOrderDelivered(order);
    }

    /**
     * *
     * Realiza un cargo a la cuenta
     *
     * @param _to
     * @param amount
     * @param reason
     * @return
     */
    public Transaction deposit(BalanceAccount _to, double amount, String reason) {
        _to.sum(amount);
        Transaction createTransaction = transactionService.createTransaction(_to.getId(), amount,
                reason, Transaction.TransactionType.DEPOSIT);
        balanceAccountService.save(_to);
        return createTransaction;
    }

    /**
     * *
     * Realiza un cargo a la cuenta
     *
     * @param _to
     * @param amount
     * @param reason
     * @return
     */
    public Transaction charge(BalanceAccount _to, double amount, String reason) {
        _to.sub(amount);
        Transaction createTransaction = transactionService.createTransaction(_to.getId(), amount,
                reason, Transaction.TransactionType.CHARGE);
        balanceAccountService.save(_to);
        return createTransaction;
    }

    /**
     * *
     * Realiza un retiro o registra uno a la cuenta
     *
     * @param _to
     * @param amount
     * @param reason
     * @return
     */
    public Transaction withdrawal(BalanceAccount _to, double amount, String reason) {
        _to.sub(amount);
        Transaction createTransaction = transactionService.createTransaction(_to.getId(), amount,
                reason, Transaction.TransactionType.WITHDRAWAL);
        balanceAccountService.save(_to);
        return createTransaction;
    }

    /**
     * *
     * Genera una transferencia de una cuenta a otra
     *
     * @param _from
     * @param _to
     * @param amount
     * @param reason
     * @return from's transaction
     */
    public Transaction transfer(BalanceAccount _from, BalanceAccount _to, double amount, String reason) {
        _from.sub(amount);
        _to.sum(amount);

        Transaction createTransaction = transactionService.createTransaction(_from.getId(), amount,
                reason, Transaction.TransactionType.CHARGE);
        transactionService.createTransaction(_to.getId(), amount,
                reason, Transaction.TransactionType.DEPOSIT);

        balanceAccountService.save(_from);

        balanceAccountService.save(_to);

        return createTransaction;

    }

    @Transactional
    public boolean executeContractPostOrderDelivered(Order order) {

        BusinessContract contract = order.getBusiness().getBusinessContract();
        BalanceAccount balanceAccount = order.getBusiness().getBalanceAccount();
        Double amount = (double) contract.getServiceCost();

        switch (contract.getServiceType()) {

            case BusinessContract.ServiceType.PER_ORDER:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(amount);
                break;

            case BusinessContract.ServiceType.PER_ORDER_PERCENTAGE:
                double percentageRate = contract.getServiceCost(); // 6% en este caso
                double percentageAmount = order.getDeliveryCost() * (percentageRate / 100);

                transactionService.createTransaction(balanceAccount.getId(), percentageAmount,
                        "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);

                balanceAccount.sub(percentageAmount);
                break;

            case BusinessContract.ServiceType.SHIPMENT_CHARGED_TO_BUSINESS:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(amount);

                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered (ship)" + order.getId(), Transaction.TransactionType.CHARGE);
                balanceAccount.sub(order.getDeliveryCost());
                break;

            default:
                transactionService.createTransaction(balanceAccount.getId(), amount, "Order delivered " + order.getId(), Transaction.TransactionType.CHARGE);

                break;

        }

        balanceAccountService.save(balanceAccount);

        return true;

    }

    public TransferDTO executeTransferDTO(TransferDTO transaction) {

        BalanceAccount _to = balanceAccountService.findById(transaction.getTo());
        if (_to == null) {
            return null;
        }

        /*si from no es nullo, por default es un transfer de from to to*/
        if (transaction.getFrom() > 0) {
            BalanceAccount _from = balanceAccountService.findById(transaction.getFrom());

            Transaction transfer = transfer(_from, _to, transaction.getAmount(), transaction.getReason());
            transaction.setId(transfer.getId());
            return transaction;

        }

        switch (transaction.getType()) {

            case Transaction.TransactionType.CHARGE:

                Transaction charge = charge(_to, transaction.getAmount(), transaction.getReason());
                transaction.setId(charge.getId());
                break;

            case Transaction.TransactionType.DEPOSIT:

                Transaction deposit = deposit(_to, transaction.getAmount(), transaction.getReason());
                transaction.setId(deposit.getId());
                break;

            case Transaction.TransactionType.WITHDRAWAL:

                Transaction withdrawal = withdrawal(_to, transaction.getAmount(), transaction.getReason());
                transaction.setId(withdrawal.getId());
                break;

        }

        return transaction;

    }

}
