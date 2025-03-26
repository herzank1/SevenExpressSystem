/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.Embeddable;

/**
 *
 * @author DeliveryExpress
 */

@Embeddable
public class Tags {

    private String data;

    public Tags() {
        this.data = "";

    }

    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        tag = formatTag(tag);
        if (!contains(tag)) {
            data = (data + " " + tag).trim();
        }
    }

    public void removeTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        tag = formatTag(tag);
        data = data.replace(tag, "").replaceAll("\\s+", " ").trim();
    }

    public boolean contains(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return false;
        }
        tag = formatTag(tag);
        return (" " + data + " ").contains(" " + tag + " ");
    }

    public String getTags() {
        return data;
    }

    @Override
    public String toString() {
        return getTags();
    }

    private String formatTag(String tag) {
        return tag.trim().replace(" ", "_").toUpperCase();
    }
public static class BusinessTags {

    private static final String IS_PREMIUM = "IS_PREMIUM";
    private static final String ALLOW_ADDRESS_SUGGESTIONS = "ALLOW_ADDRESS_SUGGESTIONS";
    private static final String ALLOW_PACKAGE_ORDERS = "ALLOW_PACKAGE_ORDERS";
    private static final String ALLOW_FOOD_ORDERS = "ALLOW_FOOD_ORDERS";

    // Agregar etiqueta IS_PREMIUM
    public static void setPremium(Tags tags) {
        tags.addTag(IS_PREMIUM);
    }

    // Eliminar etiqueta IS_PREMIUM
    public static void removePremium(Tags tags) {
        tags.removeTag(IS_PREMIUM);
    }

    // Verificar si es Premium
    public static boolean isPremium(Tags tags) {
        return tags.contains(IS_PREMIUM);
    }

    // Permitir sugerencias de dirección
    public static void setAddressSuggestions(Tags tags) {
        tags.addTag(ALLOW_ADDRESS_SUGGESTIONS);
    }

    // Eliminar sugerencias de dirección
    public static void removeAddressSuggestions(Tags tags) {
        tags.removeTag(ALLOW_ADDRESS_SUGGESTIONS);
    }

    // Verificar si permite sugerencias de dirección
    public static boolean hasAddressSuggestions(Tags tags) {
        return tags.contains(ALLOW_ADDRESS_SUGGESTIONS);
    }

    // Permitir órdenes de paquetes
    public static void setPackageOrders(Tags tags) {
        tags.addTag(ALLOW_PACKAGE_ORDERS);
    }

    // Eliminar órdenes de paquetes
    public static void removePackageOrders(Tags tags) {
        tags.removeTag(ALLOW_PACKAGE_ORDERS);
    }

    // Verificar si permite órdenes de paquetes
    public static boolean hasPackageOrders(Tags tags) {
        return tags.contains(ALLOW_PACKAGE_ORDERS);
    }

    // Permitir órdenes de comida
    public static void setFoodOrders(Tags tags) {
        tags.addTag(ALLOW_FOOD_ORDERS);
    }

    // Eliminar órdenes de comida
    public static void removeFoodOrders(Tags tags) {
        tags.removeTag(ALLOW_FOOD_ORDERS);
    }

    // Verificar si permite órdenes de comida
    public static boolean hasFoodOrders(Tags tags) {
        return tags.contains(ALLOW_FOOD_ORDERS);
    }
}


}
