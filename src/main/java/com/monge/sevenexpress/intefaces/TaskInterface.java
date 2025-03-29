/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.intefaces;

import com.monge.sevenexpress.entities.Task;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 *
 * @author DeliveryExpress
 */
public interface TaskInterface {

    public abstract void checkAndRunMissedTask();

    public abstract void executeTask();  // Ejecuta la tarea programada

    public abstract LocalDateTime getLastExecution();

    public abstract void saveLastExecution(Task task);

    public default LocalDateTime getLastFridayAt(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastFriday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
        return lastFriday;
    }

}
