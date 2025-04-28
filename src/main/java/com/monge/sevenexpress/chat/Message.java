/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.chat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.monge.sevenexpress.entities.dto.SendMessageDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Table(name = "room_messages")  // Asegúrate de que este nombre coincida con la tabla real

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  // Identificador único del mensaje
    @Column(name = "sender_id")  // Renombrar 'from' a 'sender_id' para evitar conflictos
    private String from;  // El ID del usuario que envía el mensaje

    private LocalDateTime timestamp;  // Fecha y hora de envío del mensaje

    private String content;  // El contenido del mensaje (puede ser texto o la URL de una imagen)

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false) // Clave foránea que enlaza con Room
    @JsonBackReference
    private Room room;

    @Enumerated(EnumType.STRING)
    private MessageType type;  // Tipo de mensaje (texto o imagen)

    // Constructor vacío requerido por JPA
    public Message() {
    }

    /*map temporal para menejar actualizaciones*/
    @Transient
    private Map<String, Boolean> seentBy = new HashMap<>();

    // Método para crear el mensaje a partir de un DTO
    public Message(SendMessageDTO messageDTO) {
        this.from = messageDTO.getFrom();
        this.timestamp = LocalDateTime.now();  // La fecha y hora se establece automáticamente
        this.content = messageDTO.getContent();
        this.type = messageDTO.getType();

    }

    // Enum que define los tipos de mensajes: texto o imagen
    public enum MessageType {
        TEXT, IMAGE
    }
}
