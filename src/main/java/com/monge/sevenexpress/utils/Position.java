/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.utils;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Position {

    private double latitude;
    private double longitude;

    // Constructor que recibe un String en formato "lat, long"
    public Position(String coordinates) {
        // Regex para extraer los valores de latitud y longitud del String
        Pattern pattern = Pattern.compile("([-+]?\\d*\\.\\d+),\\s?([-+]?\\d*\\.\\d+)");
        Matcher matcher = pattern.matcher(coordinates);

        if (matcher.find()) {
            this.latitude = Double.parseDouble(matcher.group(1));
            this.longitude = Double.parseDouble(matcher.group(2));
        } else {
            throw new IllegalArgumentException("Formato de coordenadas inválido");
        }
    }

    // Constructor que recibe lat y long como parámetros
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Función para calcular la distancia en km entre dos posiciones
    public double calculateDistance(Position other) {
        final int R = 6371; // Radio de la Tierra en km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Devuelve la distancia en km
    }

    // Función para generar la URL de Google Maps para mostrar la posición
    public String getGoogleMapsUrl() {
        return "https://maps.google.com/?q=" + this.latitude + "," + this.longitude;
    }

    // Función para generar una URL de navegación desde la posición actual a la de esta instancia
    public String getNavigationUrl(Position currentPosition) {
        return "https://www.google.com/maps/dir/?api=1&origin=" + currentPosition.getLatitude() + "," + currentPosition.getLongitude()
                + "&destination=" + this.latitude + "," + this.longitude + "&travelmode=driving";
    }
}
