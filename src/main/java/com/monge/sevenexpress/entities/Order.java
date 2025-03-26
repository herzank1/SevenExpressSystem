/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.monge.sevenexpress.dto.NewOrderRequest;
import com.monge.sevenexpress.enums.OrderStatus;
import com.monge.sevenexpress.enums.OrderType;
import com.monge.sevenexpress.utils.AsignationCountDown;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Entity
@Table(name = "orders")  // Cambia el nombre de la tabla a "orders"
public class Order {

    @Transient
    private static final String dateFormat = ("yyyy-MM-dd HH:mm:ss");
    @Transient
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(dateFormat);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;  // Usamos UUID en lugar de Long

    private String creationDate;
    private String closeDate;

    private OrderStatus status;
    private OrderType orderType;
    private int preparationTime;

    @ManyToOne
    @JoinColumn(name = "business_id", referencedColumnName = "id")
    @JsonManagedReference // Se serializa y maneja como la "parte principal" de la relación
    private Business business;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")  // Relación con Customer

    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    
    private String customerNote;

    private float orderCost;
    private float deliveryCost;

    @ManyToOne
    @JoinColumn(name = "order_log_id", referencedColumnName = "id")  // Relación con OrderLog

    private OrderLog orderLog;
    
    @Transient
    
    
  
    private boolean arrivedToBusiness;
    
    /*indicadores para ordenes a credito o fiadas
    el repartidor no cuenta con el monto suficiente para pagar la orden al restaurante*/
    @Transient
    private boolean credit_delivery_confirmation;
    @Transient
    private boolean credit_business_confirmation;
    
    /*true si el negocio indico que el cliente ya pago al restaurante y el repartidor no debe combrar*/
    @Transient
    private boolean payed_by_customer;
    
    @Transient
    @JsonManagedReference // Se serializa y maneja como la "parte principal" de la relación
    AsignationCountDown asignationCountDown;

    public Order() {
        this.id = UUID.randomUUID();
        this.creationDate = new SimpleDateFormat(dateFormat).format(new Date());  // Formatear correctamente

        this.status = OrderStatus.PREPARANDO;
        this.preparationTime = 10;
        this.orderLog = new OrderLog();
        this.asignationCountDown = new AsignationCountDown(this);
    }

    public Order(Business business, Customer customer, NewOrderRequest newOrder) {
        this.id = UUID.randomUUID();
        this.creationDate = new SimpleDateFormat(dateFormat).format(new Date());  // Formatear correctamente

        this.status = OrderStatus.PREPARANDO;
        this.setCustomer(customer);
        this.setBusiness(business);
        this.setCustomerNote(newOrder.getCustomerNote());
        this.setOrderCost(newOrder.getOrderCost());
        this.setDeliveryCost(newOrder.getDeliveryCost());
        this.setPreparationTime(newOrder.getPreparationTime());
         this.orderLog = new OrderLog();
         this.asignationCountDown = new AsignationCountDown(this);

    }

    public float getTotal() {
        return orderCost + deliveryCost;
    }

    /**
     * *
     *
     * @return true si la orden ya esta lista
     */
    public boolean isReady() {
        if (status != OrderStatus.PREPARANDO) {
            return true;
        }

        LocalDateTime createdAt = LocalDateTime.parse(creationDate, FORMATTER);  // Usa FORMATTER correctamente
        LocalDateTime readyTime = createdAt.plusMinutes(preparationTime);
        return LocalDateTime.now().isAfter(readyTime);
    }

    /**
     * *
     *
     * @return true si la orden esta casi lista, usar para asignacion de pedidos
     * y dar un margen de recoleccion de 10 min
     */
    public boolean isAlmostReady() {
        if (status != OrderStatus.PREPARANDO) {
            return true;
        }

        LocalDateTime createdAt = LocalDateTime.parse(creationDate, FORMATTER);
        LocalDateTime readyTime = createdAt.plusMinutes(preparationTime);
        long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), readyTime);
        return minutesLeft > 0 && minutesLeft <= 10;
    }

  

}
