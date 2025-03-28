/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    
       /**
     * *
     *
     * @return un contrato basico como el de delivery express
     */
    public static BusinessContract generate_perOrderService_contract() {
        BusinessContract businessContract = new BusinessContract();
        businessContract.setInsuranceType(BusinessContract.InsuranceType.NONE);
        businessContract.setKmBase(5);
        businessContract.setKmBaseCost(45);
        businessContract.setKmExtraCost(8);
        businessContract.setPaysCuota(false);
        businessContract.setServiceType(BusinessContract.ServiceType.PER_ORDER);
        businessContract.setServiceCost(20);

        return businessContract;
    }

    /**
     * *
     * Generar un contrato de costo de servicio por porcentaje, con seguro y
     * cuota
     *
     * @return
     */
    public static BusinessContract generate_percentage_ensured_cuotable_contract() {

        BusinessContract businessContract = new BusinessContract();
        businessContract.setInsuranceType(BusinessContract.InsuranceType.EXTENDED_PLUS);
        businessContract.setKmBase(5);
        businessContract.setKmBaseCost(45);
        businessContract.setKmExtraCost(9);
        businessContract.setPaysCuota(true);
        businessContract.setServiceType(BusinessContract.ServiceType.PER_ORDER_PERCENTAGE);
        businessContract.setServiceCost(6);

        return businessContract;

    }
    
    
       

}
