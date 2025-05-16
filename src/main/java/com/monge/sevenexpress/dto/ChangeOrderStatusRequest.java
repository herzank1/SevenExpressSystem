/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monge.sevenexpress.enums.OrderStatus;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ChangeOrderStatusRequest extends ApiRequest {

    private UUID orderId;
    OrderStatus newStatus;
    UserIndication indication;
    String position; //only Delivery
    String note;//en caso de cancelarion indicar reason

    public ChangeOrderStatusRequest() {
        super("changeOrderStatus");
    }

    public String getMetaData() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        // Método auxiliar para agregar valores nulos o vacíos
        addIfNotNull(jsonNode, "indication", indication != null ? indication.toString() : "");
        addIfNotNull(jsonNode, "position", position != null ? position : "");
        addIfNotNull(jsonNode, "note", note != null ? note : "");

        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";  // Retornar un objeto vacío en caso de error
        }
    }

    private void addIfNotNull(ObjectNode jsonNode, String fieldName, String value) {
        jsonNode.put(fieldName, value);
    }

    public enum UserIndication {

        NONE,
        SET_READY,
        ARRIVED_TO_BUSINESS,
        ARRIVED_TO_CUSTOMER;
    }


}
