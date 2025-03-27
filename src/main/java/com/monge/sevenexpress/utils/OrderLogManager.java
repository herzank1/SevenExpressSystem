/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import com.monge.sevenexpress.dto.ChangeOrderStatusRequest;
import com.monge.sevenexpress.dto.DeliveryTakeOrRejectOrder;
import com.monge.sevenexpress.entities.Log;
import java.util.ArrayList;
import java.util.Date;


public class OrderLogManager {

    

    public static void addLog(ArrayList<String>logs,ChangeOrderStatusRequest cosr) {
        logs.add(new Log(cosr).toString());
    }

    public static void addLog(ArrayList<String>logs,DeliveryTakeOrRejectOrder dtoro) {
        logs.add(new Log(dtoro).toString());
    }

 
  
}
