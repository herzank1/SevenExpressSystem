/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

/**
 *
 * @author DeliveryExpress
 */
import com.monge.sevenexpress.utils.AsignationCountDown;
import com.monge.sevenexpress.utils.AsignationCountDown.DeliveryConfirmationStatus;
import lombok.Data;
import java.util.List;

@Data
public class AsignationCountDownDTO {

    private DeliveryConfirmationStatus deliveryConfirmation;

    public AsignationCountDownDTO(AsignationCountDown asignationCountDown) {
    
        this.deliveryConfirmation = asignationCountDown.getDeliveryConfirmation();
    }
}
