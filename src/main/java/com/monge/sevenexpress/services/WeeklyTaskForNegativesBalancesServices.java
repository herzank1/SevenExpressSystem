/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.Transaction;
import com.monge.sevenexpress.entities.WeeklyTaskForNegativeBalances;
import com.monge.sevenexpress.repositories.WeeklyTaskForNegativeBalancesRepository;

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
 * @author DeliveryExpress
 */
@Service
public class WeeklyTaskForNegativesBalancesServices {

    private final BusinessService businessService;

    private final WeeklyTaskForNegativeBalancesRepository weeklyTaskForNegativeBalancesRepository;

    @Autowired
    public WeeklyTaskForNegativesBalancesServices(BusinessService businessService,
            WeeklyTaskForNegativeBalancesRepository weeklyTaskForNegativeBalancesRepository) {
        this.businessService = businessService;
        this.weeklyTaskForNegativeBalancesRepository = weeklyTaskForNegativeBalancesRepository;
    }

    // 🔹 Se ejecuta automáticamente los viernes a las 11:00 PM
    @Scheduled(cron = "0 30 23 * * FRI")
    @Transactional
    public void executeWeeklyTask() {

        WeeklyTaskForNegativeBalances weeklyTaskForNegativeBalances = new WeeklyTaskForNegativeBalances(LocalDateTime.now());
        System.out.println("✅ Ejecutando tarea programada: " + LocalDateTime.now());

        double totalDeb = 0;
        double totalBusinessProcesed = 0;

        List<Business> findAll = businessService.getBusinessRepository().findAll();
        for (Business business : findAll) {

            BusinessContract businessContract = businessService.getBusinessContract(business);

            BalanceAccount balanceAccount = businessService.getBalanceAccount(business);

            double maximumDebt = businessContract.getMaximumDebt();
            double balance = balanceAccount.getBalance();

            if (balance > -maximumDebt) {
                business.setAccountStatus(Business.AccountStatus.SUSPENDIDO);
                businessService.save(business);

                totalDeb += balance;

            }

            totalBusinessProcesed++;

        }

        weeklyTaskForNegativeBalances.setTotalNegative(totalDeb);
        weeklyTaskForNegativeBalances.setTotalBusinesess(totalBusinessProcesed);

        saveLastExecution(weeklyTaskForNegativeBalances);
    }

    // 🔹 Verifica al iniciar si la última ejecución fue el viernes pasado
    private void checkAndRunMissedTask() {
        LocalDateTime lastExecution = getLastExecution();
        LocalDateTime lastFriday = getLastFridayAt(23, 30);

        if (lastExecution == null || lastExecution.isBefore(lastFriday)) {
            System.out.println("⚠ No se ejecutó la tarea el viernes pasado. Ejecutando ahora...");
            executeWeeklyTask();
        } else {
            System.out.println("✅ La tarea ya fue ejecutada el viernes pasado.");
        }
    }

    // 🔹 Obtiene la última ejecución desde la base de datos
    private LocalDateTime getLastExecution() {
        WeeklyTaskForNegativeBalances lastExecution = weeklyTaskForNegativeBalancesRepository.findTopByOrderByExecutionDateDesc();
        return lastExecution != null ? lastExecution.getExecutionDate() : null;
    }

    // 🔹 Guarda la última ejecución en la base de datos
    private void saveLastExecution(WeeklyTaskForNegativeBalances weeklyTaskForNegativeBalances) {

        weeklyTaskForNegativeBalancesRepository.save(weeklyTaskForNegativeBalances);
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
