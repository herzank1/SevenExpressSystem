/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DeliveryExpress
 */
public class LoggerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // Habilita "pretty print"

    public static void printAsJson(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            logger.info("\n{}", json); // Agrega un salto de línea para mejor formato en consola
        } catch (Exception e) {
            logger.error("Error al convertir el objeto a JSON", e);
        }
    }

    public static void printInfo(String data) {
        logger.info(data);
    }

    public static void printWarn(String data) {
        logger.warn(data);
    }

    public static void printError(String data) {
        logger.error(data);
    }

}
