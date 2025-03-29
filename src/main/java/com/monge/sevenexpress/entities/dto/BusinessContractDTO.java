/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

/**
 *
 * @author DeliveryExpress
 */
import com.monge.sevenexpress.entities.BusinessContract;
import lombok.Data;

@Data
public class BusinessContractDTO {
    private Long id;
    private float kmBaseCost;
    private float kmBase;
    private float kmExtraCost;
    private boolean paysCuota;
    private float serviceCost;
    private String serviceType;
    private String insuranceType;
    private double maximumDebt;
    private String ordersType;

    public BusinessContractDTO(BusinessContract businessContract) {
        this.id = businessContract.getId();
        this.kmBaseCost = businessContract.getKmBaseCost();
        this.kmBase = businessContract.getKmBase();
        this.kmExtraCost = businessContract.getKmExtraCost();
        this.paysCuota = businessContract.isPaysCuota();
        this.serviceCost = businessContract.getServiceCost();
        this.serviceType = businessContract.getServiceType().toString();
        this.insuranceType = businessContract.getInsuranceType().toString();
        this.maximumDebt = businessContract.getMaximumDebt();
        this.ordersType = businessContract.getOrdersType().toString();
    }
}
