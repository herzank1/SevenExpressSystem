/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Esto para poder referenciar un ID único de OrderLog

    @ElementCollection
    @CollectionTable(name = "order_log_entries", joinColumns = @JoinColumn(name = "order_log_id"))
    private List<Log> logs = new ArrayList<>();

    public void addLog(ChangeOrderStatusRequest cosr) {
        logs.add(new Log(cosr));
    }

    public void addLog(DeliveryTakeOrRejectOrder dtoro) {
        logs.add(new Log(dtoro));
    }

    @Data
    @Embeddable // Marca la clase Log como embebible
    public static class Log {

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
            this.date  = new Date().toString();
            if (changeOrderStatusRequest.getNewStatus() != null) {
                this.event = changeOrderStatusRequest.getNewStatus().name();
            }

            if (changeOrderStatusRequest.getIndication() != null) {
                this.event = changeOrderStatusRequest.getIndication().name();
            }

            this.sender = changeOrderStatusRequest.getRequesterId() + " - " + changeOrderStatusRequest.getRequesterType();
            this.data = changeOrderStatusRequest.getMetaData();
        }

        private Log(DeliveryTakeOrRejectOrder dtoro) {

            this.date = new Date().toString();

            this.event = "take:" + dtoro.isTake();

            this.sender = dtoro.getRequesterId() + " - " + dtoro.getRequesterType();
            this.data = "[]";

        }

    }
}
