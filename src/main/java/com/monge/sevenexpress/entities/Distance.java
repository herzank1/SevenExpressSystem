/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.text.Normalizer;
import lombok.Data;

@Data
@Entity
public class Distance {

    @Id
    private String id; // Hash de origin + destination

    @Column(length = 512)
    private String origin;

    @Column(length = 512)
    private String destination;

    private double distanceKm;
    private double durationMin;

    public Distance() {
    }

    public Distance(String origin, String destination, double distanceKm, double durationMin) {
        this.origin = normalize(origin);
        this.destination = normalize(destination);
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
        this.id = generateId(this.origin, this.destination);
    }

    public static String generateId(String origin, String destination) {
        String combined = normalize(origin) + "|" + normalize(destination);
        return Integer.toHexString(combined.hashCode());
    }

    private static String normalize(String input) {
        return input == null ? "" :
            Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\s+", " "); // Quitar acentos y reducir espacios
    }
}
