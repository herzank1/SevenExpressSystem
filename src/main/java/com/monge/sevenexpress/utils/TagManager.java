/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class TagManager {

    /**
     * *
     *
     * @param tags container
     * @param tag tag tobe added
     */
    public static void addTag(ArrayList<String> tags, String... tag) {
        for (String t : tag) {
            if (!contains(tags, t)) {
                tags.add(formatTag(t));
            }
        }

    }

    public static boolean removeTag(ArrayList<String> tags, String tag) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }

        return tags.remove(tag);
    }

    /**
     * *
     *
     * @param tags tags container arrayList<String>
     * @param tag tagname
     * @return
     */
    public static boolean contains(ArrayList<String> tags, String tag) {
        if (tags == null || tags.isEmpty()) {
            return false;
        }

        return tags.contains(tags);
    }

    private static String formatTag(String tag) {
        return tag.trim().replace(" ", "_").toUpperCase();
    }

    public static class BusinessTags {

        private static final String IS_PREMIUM = "IS_PREMIUM";
        private static final String ALLOW_ADDRESS_SUGGESTIONS = "ALLOW_ADDRESS_SUGGESTIONS";
        private static final String ALLOW_PACKAGE_ORDERS = "ALLOW_PACKAGE_ORDERS";
        private static final String ALLOW_FOOD_ORDERS = "ALLOW_FOOD_ORDERS";

        // Agregar etiqueta IS_PREMIUM
        public static void setPremium(ArrayList<String> tags) {
            TagManager.addTag(tags, IS_PREMIUM);
        }

        // Eliminar etiqueta IS_PREMIUM
        public static void removePremium(ArrayList<String> tags) {
            TagManager.removeTag(tags, IS_PREMIUM);
        }

        // Verificar si es Premium
        public static boolean isPremium(ArrayList<String> tags) {
            return TagManager.contains(tags, IS_PREMIUM);
        }

        // Permitir sugerencias de dirección
        public static void setAddressSuggestions(ArrayList<String> tags) {
            TagManager.addTag(tags, ALLOW_ADDRESS_SUGGESTIONS);
        }

        // Eliminar sugerencias de dirección
        public static void removeAddressSuggestions(ArrayList<String> tags) {
            TagManager.removeTag(tags, ALLOW_ADDRESS_SUGGESTIONS);
        }

        // Verificar si permite sugerencias de dirección
        public static boolean hasAddressSuggestions(ArrayList<String> tags) {
            return TagManager.contains(tags, ALLOW_ADDRESS_SUGGESTIONS);
        }

        // Permitir órdenes de paquetes
        public static void setPackageOrders(ArrayList<String> tags) {
            TagManager.addTag(tags, ALLOW_PACKAGE_ORDERS);
        }

        // Eliminar órdenes de paquetes
        public static void removePackageOrders(ArrayList<String> tags) {
            TagManager.removeTag(tags, ALLOW_PACKAGE_ORDERS);
        }

        // Verificar si permite órdenes de paquetes
        public static boolean hasPackageOrders(ArrayList<String> tags) {
            return TagManager.contains(tags, ALLOW_PACKAGE_ORDERS);
        }

        // Permitir órdenes de comida
        public static void setFoodOrders(ArrayList<String> tags) {
            TagManager.addTag(tags, ALLOW_FOOD_ORDERS);
        }

        // Eliminar órdenes de comida
        public static void removeFoodOrders(ArrayList<String> tags) {
            TagManager.removeTag(tags, ALLOW_FOOD_ORDERS);
        }

        // Verificar si permite órdenes de comida
        public static boolean hasFoodOrders(ArrayList<String> tags) {
            return TagManager.contains(tags, ALLOW_FOOD_ORDERS);
        }
    }

}
