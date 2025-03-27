/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import jakarta.persistence.Transient;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author DeliveryExpress
 */
public class DateUtils {
    
      
    public static final String dateFormat = ("yyyy-MM-dd HH:mm:ss");
    
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(dateFormat);

    
}
