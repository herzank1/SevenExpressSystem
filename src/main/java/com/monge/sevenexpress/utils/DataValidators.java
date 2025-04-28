/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

/**
 *
 * @author Diego Villarreal
 */
public class DataValidators {

    /**
     * Valida si un nombre de usuario es válido según las siguientes reglas: -
     * Debe tener entre 3 y 24 caracteres de longitud. - Solo puede contener
     * letras (mayúsculas o minúsculas), números y guiones bajos (_). - No se
     * permiten espacios ni caracteres especiales.
     *
     * @param username El nombre de usuario a validar.
     * @return true si el nombre de usuario es válido, false en caso contrario.
     */
    public static boolean isValidUserName(String username) {
        // Verifica que el nombre no sea nulo y que cumpla con el patrón especificado
        return username != null && username.matches("^[a-zA-Z0-9_]{3,24}$");
    }

    /**
     * Valida si una contraseña es válida según las siguientes reglas: - Debe
     * tener entre 8 y 24 caracteres de longitud. - Puede contener cualquier
     * carácter, excepto espacios.
     *
     * @param password La contraseña a validar.
     * @return true si la contraseña es válida, false en caso contrario.
     */
    public static boolean isValidPassword(String password) {
        // Verifica que la contraseña no sea nula y que no contenga espacios,
        // y que tenga entre 8 y 24 caracteres
        return password != null && password.matches("^[^\\s]{8,24}$");
    }

    /**
     * Valida si un número de teléfono es válido según las siguientes reglas: -
     * Debe tener exactamente 10 dígitos numéricos. - No se permiten letras,
     * espacios ni caracteres especiales.
     *
     * @param phoneNumber El número de teléfono a validar.
     * @return true si el número es válido, false en caso contrario.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Verifica que el número no sea nulo y contenga exactamente 10 dígitos
        return phoneNumber != null && phoneNumber.matches("^\\d{10}$");
    }

    /**
     * Valida si una dirección es válida según las siguientes reglas: - Debe
     * tener entre 5 y 100 caracteres.
     *
     * @param address La dirección a validar.
     * @return true si es válida, false en caso contrario.
     */
   public static boolean isValidAddress(String address) {
    return address != null && address.length() >= 5 && address.length() <= 150
            && address.matches("^[\\p{L}0-9\\s.,#\\-/°ºª'\"()&]+$");
}


    /**
     * Valida si una cadena representa coordenadas GPS válidas. Ejemplo válido:
     * "32.651292619771155, -115.53897383583057"
     *
     * @param coordinates Las coordenadas en formato "latitud, longitud".
     * @return true si las coordenadas son válidas, false en caso contrario.
     */
    public static boolean isValidCoordinates(String coordinates) {
        if (coordinates == null) {
            return false;
        }

        // Regex para validar el formato general
        String regex = "^[-+]?\\d{1,3}(\\.\\d+)?\\s*,\\s*[-+]?\\d{1,3}(\\.\\d+)?$";

        if (!coordinates.matches(regex)) {
            return false;
        }

        // Separar la latitud y longitud
        String[] parts = coordinates.split(",");
        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lng = Double.parseDouble(parts[1].trim());

            // Validar rangos
            return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
