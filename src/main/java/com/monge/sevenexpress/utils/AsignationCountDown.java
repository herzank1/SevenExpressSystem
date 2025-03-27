/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.Order;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class AsignationCountDown {

    //referenced order
   // @JsonBackReference
    @JsonIgnore
    Order order;
    ArrayList<Long> blackList = new ArrayList<>();
    DeliveryConfirmationStatus deliveryConfirmation;

    // Usamos ScheduledExecutorService para manejar temporizadores de manera eficiente
    @Transient
    @JsonIgnore
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Transient
    @JsonIgnore
    private ScheduledFuture<?> countdownTask;  // Referencia a la tarea programada

    public AsignationCountDown(Order order) {
        this.order = order;
    }

    // Inicia el temporizador
    private void startCountDown() {
        // Definir un retraso de ejemplo, puedes cambiar esto según tu lógica
        long delayInSeconds = 10; // por ejemplo, 10 segundos

        // Programar la tarea y almacenar la referencia a la tarea
        countdownTask = scheduler.schedule(() -> {
            // Comprobamos si el estado sigue siendo WAITING_DELIVERY_CONFIRMATION
            if (deliveryConfirmation == DeliveryConfirmationStatus.WAITING_DELIVERY_CONFIRMATION) {
                reject(); // Si está esperando confirmación, rechazamos
            }
        }, delayInSeconds, TimeUnit.SECONDS);
    }

    // Detiene solo la tarea actual (sin detener todos los hilos)
    private void stopCountDown() {
        if (countdownTask != null && !countdownTask.isDone()) {
            countdownTask.cancel(false);  // Cancela la tarea, pero no interrumpe el hilo
        }
    }

    /* Método por el asignador automático */
    public void assign(Delivery delivery, boolean force) {
        if (force || !blackList.contains(delivery.getId())) {
            order.setDelivery(delivery);
            deliveryConfirmation = DeliveryConfirmationStatus.WAITING_DELIVERY_CONFIRMATION;
            startCountDown();
        }
    }

    public void take() {
        deliveryConfirmation = DeliveryConfirmationStatus.COMFIRMED;
        stopCountDown();
    }

    public void reject() {
        deliveryConfirmation = DeliveryConfirmationStatus.NONE;
        if (!blackList.contains(order.getDelivery().getId())) {
            blackList.add(order.getDelivery().getId());
        }

        order.setDelivery(null);
        stopCountDown();

    }

    // Función que verifica si la entrega puede ser asignada
    public boolean isAsignable() {
        // Verifica que no haya entrega asignada, que el temporizador no esté en ejecución, y que la confirmación sea NONE
        return order.getDelivery() == null
                && (countdownTask == null || countdownTask.isDone())
                && deliveryConfirmation == DeliveryConfirmationStatus.NONE;
    }

    public boolean isConfirmed() {
        // Verifica que la entrega no sea null, el temporizador esté detenido, y la confirmación sea COMFIRMED
        return order.getDelivery() != null
                && (countdownTask == null || countdownTask.isDone())
                && deliveryConfirmation == DeliveryConfirmationStatus.COMFIRMED;
    }

    public enum DeliveryConfirmationStatus {
        NONE, WAITING_DELIVERY_CONFIRMATION, COMFIRMED;
    }

}
