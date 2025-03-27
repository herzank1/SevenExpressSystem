/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Log {

    private String date;
    private String event;
    private String sender;
    private String data;

    public Log(String date, String event, String sender, String data) {
        this.date = date;
        this.event = event;
        this.sender = sender;
        this.data = data;
    }

    public Log(ChangeOrderStatusRequest changeOrderStatusRequest) {
        this.date = new Date().toString();
        if (changeOrderStatusRequest.getNewStatus() != null) {
            this.event = changeOrderStatusRequest.getNewStatus().name();
        }

        if (changeOrderStatusRequest.getIndication() != null) {
            this.event = changeOrderStatusRequest.getIndication().name();
        }

        this.sender = changeOrderStatusRequest.getRequesterId() + " - " + changeOrderStatusRequest.getRequesterType();
        this.data = changeOrderStatusRequest.getMetaData();
    }

    public Log(DeliveryTakeOrRejectOrder dtoro) {

        this.date = new Date().toString();

        this.event = "take:" + dtoro.isTake();

        this.sender = dtoro.getRequesterId() + " - " + dtoro.getRequesterType();
        this.data = "[]";

    }
    
    

}
