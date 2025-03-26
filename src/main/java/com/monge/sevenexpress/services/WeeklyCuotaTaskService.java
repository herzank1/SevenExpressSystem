/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.WeeklyCuotaTask;
import com.monge.sevenexpress.repositories.WeeklyCuotaTaskRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Servicio para pago de cuotas semanal
 */
@Service
public class WeeklyCuotaTaskService {

    private final WeeklyCuotaTaskRepository weeklyCuotaTaskRepository;

    private final BusinessService businessService;

    @Autowired
    public WeeklyCuotaTaskService(WeeklyCuotaTaskRepository weeklyCuotaTaskRepository,
            BusinessService businessService) {
        this.weeklyCuotaTaskRepository = weeklyCuotaTaskRepository;
        this.businessService = businessService;
        checkAndRunMissedTask();
    }

    // 🔹 Se ejecuta automáticamente los viernes a las 11:00 PM
    @Scheduled(cron = "0 0 23 * * FRI")
    @Transactional
    public void executeWeeklyTask() {

        WeeklyCuotaTask weklyCuotaTask = new WeeklyCuotaTask(LocalDateTime.now());
        System.out.println("✅ Ejecutando tarea programada: " + LocalDateTime.now());

        double totalCharged = 0;
        double totalBusinessCharged = 0;

        List<Business> findAll = businessService.getBusinessRepository().findAll();
        for (Business business : findAll) {

            BusinessContract businessContract = businessService.getBusinessContract(business);

            if (businessContract.isPaysCuota()) {

                BalanceAccount balanceAccount = businessService.getBalanceAccount(business);
                double amount = 99;
                businessService.getBalanceAccountService().subBalance(balanceAccount, amount);
                businessService.getTransactionService()
                        .createTransaction(balanceAccount.getId(), amount, "Pago de cuota", Transaction.TransactionType.CHARGE);

                totalBusinessCharged++;
                totalCharged += amount;

            }

        }

        weklyCuotaTask.setTotalCharged(totalCharged);
        weklyCuotaTask.setTotalBusinesess(totalBusinessCharged);

        saveLastExecution(weklyCuotaTask);
    }

    // 🔹 Verifica al iniciar si la última ejecución fue el viernes pasado
    private void checkAndRunMissedTask() {
        LocalDateTime lastExecution = getLastExecution();
        LocalDateTime lastFriday = getLastFridayAt(23, 0);

        if (lastExecution == null || lastExecution.isBefore(lastFriday)) {
            System.out.println("⚠ No se ejecutó la tarea el viernes pasado. Ejecutando ahora...");
            executeWeeklyTask();
        } else {
            System.out.println("✅ La tarea ya fue ejecutada el viernes pasado.");
        }
    }

    // 🔹 Obtiene la última ejecución desde la base de datos
    private LocalDateTime getLastExecution() {
        WeeklyCuotaTask lastExecution = weeklyCuotaTaskRepository.findTopByOrderByExecutionDateDesc();
        return lastExecution != null ? lastExecution.getExecutionDate() : null;
    }

    // 🔹 Guarda la última ejecución en la base de datos
    private void saveLastExecution(WeeklyCuotaTask weeklyCuotaTask) {

        weeklyCuotaTaskRepository.save(weeklyCuotaTask);
    }

    // 🔹 Obtiene el último viernes a la hora deseada
    private LocalDateTime getLastFridayAt(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastFriday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
        return lastFriday;
    }

}
