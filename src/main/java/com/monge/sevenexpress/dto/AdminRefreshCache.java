/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AdminRefreshCache extends ApiRequest {
    /*si id es null actualizara todo en cache*/
   private long id;
   private RefreshAction refreshAction;

    public AdminRefreshCache() {
        super("");
    }

    public enum RefreshAction {

        DELIVERY,
        USER,
        BUSINESS,
        ADMIN,

    }

}
