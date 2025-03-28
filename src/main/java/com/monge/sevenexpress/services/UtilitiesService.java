/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.subservices.GoogleMapsService;
import com.monge.sevenexpress.subservices.TokenBlacklistService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Data
@Service
public class UtilitiesService {

    @Autowired
    private GoogleMapsService googleMapsService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

}
