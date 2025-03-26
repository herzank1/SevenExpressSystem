/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Builder
public class ApiResponse<T> {

    private Status status;
    private String message;
    private T data;
    

    // Constructor para respuesta exitosa
    public ApiResponse(Status status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor para error
    public static ApiResponse error(String message) {
        return new ApiResponse(Status.error, message, null);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(Status.success, message, data);
    }

    public enum Status {

        success, error;
    }

    public String toJson() {
        // Usamos una librería como Jackson para convertir a JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";  // En caso de error, devolver un JSON vacío
        }
    }

}
