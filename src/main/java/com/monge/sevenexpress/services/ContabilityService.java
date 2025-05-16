/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.entities.PaymentReceipt;
import com.monge.sevenexpress.entities.PaymentReceipt.PaymentStatus;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.dto.TransferDTO;
import com.monge.sevenexpress.events.ChargeApiCallsEvent;
import com.monge.sevenexpress.subservices.BalanceAccountService;
import com.monge.sevenexpress.events.OnOrderDeliveredEvent;
import com.monge.sevenexpress.subservices.PaymentReceiptService;
import com.monge.sevenexpress.subservices.TransactionService;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
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
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BalanceAccountService balanceAccountService;

    @Autowired
    private PaymentReceiptService paymentReceiptService;

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

        chatService.sendMessage(_to.getId().toString(), "Has recibido un deposito!");
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
        chatService.sendMessage(_to.getId().toString(), "Se ha hecho un cargo a tu cuenta.");
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
    @EventListener
    protected boolean executeContractPostOrderDelivered(OnOrderDeliveredEvent event) {

        Order order = event.getOrder();

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

        BalanceAccount _to = balanceAccountService.getById(transaction.getTo());
        if (_to == null) {
            return null;
        }

        /*si from no es nullo, por default es un transfer de from to to*/
        if (transaction.getFrom() > 0) {
            BalanceAccount _from = balanceAccountService.getById(transaction.getFrom());

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

    /**
     * *
     * Todos los recibos de pago son saldo positivo
     *
     * @param id
     * @param status
     * @return
     */
    public PaymentReceipt updatePaymentReceipt(long id, PaymentReceipt.PaymentStatus status) {

        PaymentReceipt payment = paymentReceiptService.getPaymentReceiptRepository().findById(id).orElse(null);
        if (payment == null) {
            return null;
        }

        Business business = userService.getBusinessService().getBusinessRepository().findByBalanceAccountId(payment.getBalanceAccountId()).orElse(null);

        if (business == null) {
            return null;
        }

        BalanceAccount balAccount = business.getBalanceAccount();

        boolean success = false;

        switch (status) {

            case PaymentStatus.PROCESSED:

                /*evitamos el doble procesamiento*/
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    balAccount.sum(payment.getAmount());
                    balAccount = balanceAccountService.save(balAccount);
                    transactionService.createTransaction(balAccount.getId(), payment.getAmount(), "Pago de servicios", Transaction.TransactionType.DEPOSIT);
                    payment.setStatus(status);
                    paymentReceiptService.getPaymentReceiptRepository().save(payment);

                    onBusinessPaymentAproved(business);
                    chatService.sendMessage(balAccount.getId().toString(), "Tu pago se ha confirmado, ya puedes enviar pedidos.");

                    success = true;

                }

                break;

            case PaymentStatus.REJECTED:
                payment.setStatus(status);
                paymentReceiptService.getPaymentReceiptRepository().save(payment);

                break;

            default:
                break;

        }

        return payment;
    }

    public void onBusinessPaymentAproved(Business business) {
        //userService

        /*si business no excede su deuda*/
        if (!business.exceedsItsDebt()) {
            business.setAccountStatus(Business.AccountStatus.ACTIVADO);
            userService.getBusinessService().save(business);
        }

    }

    @EventListener
    protected void executeChargeApiCalls(ChargeApiCallsEvent event) {

        ConcurrentHashMap<Long, Double> userApiUsage = event.getUserApiUsage();
        Iterator<Map.Entry<Long, Double>> iterator = userApiUsage.entrySet().iterator();

        // Recorrer todas las entradas del mapa
        while (iterator.hasNext()) {
            // Obtener cada entrada (key, value)
            Map.Entry<Long, Double> entry = iterator.next();

            Long key = entry.getKey();
            Double value = entry.getValue();
            BalanceAccount findById = balanceAccountService.getById(key);
            if (findById != null) {
                findById.sub(value);
                balanceAccountService.save(findById);

                transactionService.createTransaction(key, value, "Miselaneos", Transaction.TransactionType.CHARGE);
            }

        }

    }

    @Scheduled(fixedRate = 900000) // 15 minutos en milisegundos
    public void cacheCleaner() {
        balanceAccountService.clearCache();

    }

}
