/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.enums.OrderType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Entity
public class BusinessContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float kmBaseCost;
    private float kmBase;
    private float kmExtraCost;
    private boolean paysCuota;
    private float serviceCost;//si el Service Type es PER_ORDER_PERCENTAGE usarce como %
    private ServiceType serviceType;
    private InsuranceType insuranceType;
    private double maximumDebt;
    private OrderType ordersType;

   
    
    /***
     * deaful contructor
     */
    public BusinessContract() {
        this.kmBaseCost = 45;
        this.kmBase = 5;
        this.kmExtraCost = 8;
        this.paysCuota = false;
        this.serviceCost = 20;
        this.serviceType = ServiceType.PER_ORDER;
        this.insuranceType = InsuranceType.NONE;
        this.maximumDebt = 500;
        this.ordersType = OrderType.FOOD;
    }

    public enum ServiceType {
        PER_ORDER("PER_ORDER"),
        PER_ORDER_PERCENTAGE("PER_ORDER_PERCENTAGE"),
        WEEKLY("WEEKLY"),
        MONTHLY("MONTHLY"),
        DISTANCE("DISTANCE"),
        SHIPMENT_CHARGED_TO_BUSINESS("SHIPMENT_CHARGED_TO_BUSINESS");

        private final String value;

        ServiceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum InsuranceType {
        NONE("NONE"),
        PLUS("PLUS"),
        EXTENDED_PLUS("EXTENDED_PLUS");

        private final String value;

        InsuranceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    
       

}
