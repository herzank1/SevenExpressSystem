/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices.tasks;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.BusinessContract;
import com.monge.sevenexpress.entities.WeeklyCuotaTask;
import com.monge.sevenexpress.entities.WeeklyDisableDefaultersTask;
import com.monge.sevenexpress.intefaces.AbstractTask;
import com.monge.sevenexpress.intefaces.TaskInterface;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.monge.sevenexpress.repositories.TaskRepository;
import com.monge.sevenexpress.services.ContabilityService;
import com.monge.sevenexpress.subservices.BusinessService;

/**
 *
 * Servicio para pago de cuotas semanal
 */
@Service
public class WeeklyDisableDefaultersService implements TaskInterface {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private ContabilityService contabilityService;

    public WeeklyDisableDefaultersService() {
        checkAndRunMissedTask();
    }

    // 🔹 Se ejecuta automáticamente los viernes a las 11:00 PM
    @Scheduled(cron = "0 0 21 * * FRI")
    @Transactional
    @Override
    public void executeTask() {

        WeeklyDisableDefaultersTask weeklyDisableDefaultersTask = new WeeklyDisableDefaultersTask(LocalDateTime.now());
        weeklyDisableDefaultersTask.setTaskReason(AbstractTask.TaskReason.DISABLE_NEGATIVE_BALANCES);
        weeklyDisableDefaultersTask.setExecutionMode(AbstractTask.ExecutionMode.WEEKLY);
        System.out.println("✅ Ejecutando tarea programada: " + LocalDateTime.now());

        double totalDeb = 0;
        double totalBusinesessDisabled = 0;

        List<Business> findAll = businessService.getBusinessRepository().findAll();
        for (Business business : findAll) {

            BusinessContract businessContract = business.getBusinessContract();
            BalanceAccount balanceAccount = business.getBalanceAccount();
            /*convertimos max deb en negativo para comparar*/
            double maximumDebt = businessContract.getMaximumDebt()*-1;
            double balance = balanceAccount.getBalance();

            if (balance<maximumDebt) {

               
                totalBusinesessDisabled++;
                totalDeb += balance;

            }

        }

        weeklyDisableDefaultersTask.setTotalBusinesessDisabled(totalBusinesessDisabled);
        weeklyDisableDefaultersTask.setTotalDeb(totalDeb);

        saveLastExecution(weeklyDisableDefaultersTask);
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
    public LocalDateTime getLastExecution() {
        WeeklyCuotaTask lastExecution = (WeeklyCuotaTask) taskRepository.findTopByOrderByExecutionDateDesc();
        return lastExecution != null ? lastExecution.getExecutionDate() : null;
    }

    @Override
    public void saveLastExecution(AbstractTask task) {
        taskRepository.save(task);
    }

}
