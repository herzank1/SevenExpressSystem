/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Converter
public class HashMapConverter implements AttributeConverter<Map<String, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error convirtiendo lista a JSON", e);
        }
    }

    @Override
    public  Map<String, String> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error convirtiendo JSON a lista", e);
        }
    }
}

