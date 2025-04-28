/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities;

import com.monge.sevenexpress.entities.UserProfile.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "jwt_tokens")
@Data
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Date expirationDate;

    @Enumerated(EnumType.STRING)
    private TokenStatus status; // ACTIVADO, REVOCADO, EXPIRED

    public JwtToken() {
    }
    
    

    public JwtToken(String token, String username, Date expirationDate, TokenStatus status) {
        this.token = token;
        this.username = username;
        this.expirationDate = expirationDate;
        this.status = status;
    }
    
    
    public enum TokenStatus{
    ACTIVADO,REVOCADO,EXPIRADO;
    }

    // Getters and setters...
}
