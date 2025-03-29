/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices.tasks;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Task;
import com.monge.sevenexpress.entities.Task.ExecutionMode;
import com.monge.sevenexpress.entities.Task.TaskReason;
import com.monge.sevenexpress.entities.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.monge.sevenexpress.repositories.TaskRepository;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.subservices.BusinessService;
import jakarta.annotation.PostConstruct;
import com.monge.sevenexpress.intefaces.TaskInterface;

/**
 *
 * Servicio para pago de cuotas semanal
 */
@Service
public class WeeklyCuotaTaskService implements TaskInterface {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ContabilityService contabilityService;

       @PostConstruct
    public void init() {
        checkAndRunMissedTask();  // ✅ Se ejecutará después de la inyección de dependencias
    }

    // 🔹 Se ejecuta automáticamente los viernes a las 11:00 PM
    @Scheduled(cron = "0 0 23 * * FRI")
    @Transactional
    @Override
    public void executeTask() {

        Task task = new Task(LocalDateTime.now());
        task.setTaskReason(TaskReason.CUOTA_CHARGIN);
        task.setExecutionMode(ExecutionMode.WEEKLY);
        System.out.println("✅ Ejecutando tarea programada: " + LocalDateTime.now());

        double totalCharged = 0;
        double totalBusinessCharged = 0;

        List<Business> findAll = businessService.getBusinessRepository().findAll();
        for (Business business : findAll) {

            BusinessContract businessContract = business.getBusinessContract();
            BalanceAccount balanceAccount = business.getBalanceAccount();

            if (businessContract.isPaysCuota()) {

                double amount = 99;
                contabilityService.getBalanceAccountService().subBalance(balanceAccount, amount);
                contabilityService.getTransactionService()
                        .createTransaction(balanceAccount.getId(), amount, "Pago de cuota", Transaction.TransactionType.CHARGE);

                totalBusinessCharged++;
                totalCharged += amount;

            }

        }

        task.getData().put("totalCharged",totalCharged);
        task.getData().put("totalBusinessCharged",totalBusinessCharged);

        saveLastExecution(task);
    }

    // 🔹 Verifica al iniciar si la última ejecución fue el viernes pasado
    @Override
    public void checkAndRunMissedTask() {
        LocalDateTime lastExecution = getLastExecution();
        LocalDateTime lastFriday = getLastFridayAt(23, 0);

        if (lastExecution == null || lastExecution.isBefore(lastFriday)) {
            System.out.println("⚠ No se ejecutó la tarea el viernes pasado. Ejecutando ahora...");
            executeTask();
        } else {
            System.out.println("✅ La tarea ya fue ejecutada el viernes pasado.");
        }
    }

    // 🔹 Obtiene la última ejecución desde la base de datos
    @Override
     @Transactional
    public LocalDateTime getLastExecution() {
        Task lastExecution =  taskRepository.findMostRecentTaskByReason(TaskReason.CUOTA_CHARGIN).orElse(null);
        return lastExecution != null ? lastExecution.getExecutionDate() : null;
    }

    @Override
    public void saveLastExecution(Task task) {
        taskRepository.save(task);
    }

}
