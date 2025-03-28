/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.intefaces.AbstractTask;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 * Desabilita a morosos
 */
@Data
@EqualsAndHashCode(callSuper=false) 
@Entity
public class WeeklyDisableDefaultersTask extends AbstractTask{

    public WeeklyDisableDefaultersTask(LocalDateTime now) {
        super.setExecutionDate(now);
    }

    /*deuda total*/
    public void setTotalDeb(double totalDeb) {
    super.getData().put("totalDeb", totalDeb);
    }

    /*todal de negocios inhabilitados*/
    public void setTotalBusinesessDisabled(double TotalBusinesessDisabled) {
    super.getData().put("TotalBusinesessDisabled", TotalBusinesessDisabled);
    }
    
    
    
}
