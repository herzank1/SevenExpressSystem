/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.entities.Order;
import com.monge.sevenexpress.enums.OrderStatus;
import com.monge.sevenexpress.enums.OrderType;
import com.monge.sevenexpress.utils.AsignationCountDown;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;

@Data
public class OrderDTO {

    private UUID id;
    private String creationDate;
    private String closeDate;
    
    private OrderStatus status;
    private OrderType orderType;
    private int preparationTime;

    private BusinessDTO business;
    private CustomerDTO customer;
    private DeliveryDTO delivery;

    private String customerNote;
    
    private float orderCost;
    private float deliveryCost;

    private ArrayList<String> orderLog;

    private boolean arrivedToBusiness;
    private boolean credit_delivery_confirmation;
    private boolean credit_business_confirmation;
    private boolean payed_by_customer;


    public OrderDTO(Order order) {
        this.id = order.getId();
        this.creationDate = order.getCreationDate();
        this.closeDate = order.getCloseDate();
        this.status = order.getStatus();
        this.orderType = order.getOrderType();
        this.preparationTime = order.getPreparationTime();

        this.business = order.getBusiness() != null ? new BusinessDTO(order.getBusiness()) : null;
        this.customer = order.getCustomer() != null ? new CustomerDTO(order.getCustomer()) : null;
        this.delivery = order.getDelivery() != null ? new DeliveryDTO(order.getDelivery()) : null;

        this.customerNote = order.getCustomerNote();
        this.orderCost = order.getOrderCost();
        this.deliveryCost = order.getDeliveryCost();
        this.orderLog = order.getOrderLog();

        this.arrivedToBusiness = order.isArrivedToBusiness();
        this.credit_delivery_confirmation = order.isCredit_delivery_confirmation();
        this.credit_business_confirmation = order.isCredit_business_confirmation();
        this.payed_by_customer = order.isPayed_by_customer();
     
    }
}
