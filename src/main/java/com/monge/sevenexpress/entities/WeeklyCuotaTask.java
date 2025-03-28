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
 */
@Data
@EqualsAndHashCode(callSuper=false) 
@Entity
public class WeeklyCuotaTask extends AbstractTask{

    public WeeklyCuotaTask(LocalDateTime now) {
        super.setExecutionDate(now);
    }

    public void setTotalCharged(double totalCharged) {
    super.getData().put("totalCharge", totalCharged);
    }

    public void setTotalBusinesess(double totalBusinessCharged) {
    super.getData().put("totalBusinessCharged", totalBusinessCharged);
    }
    
    
    
}
