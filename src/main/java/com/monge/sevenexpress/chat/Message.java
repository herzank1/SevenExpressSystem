/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.chat;

import com.monge.sevenexpress.entities.dto.SendMessageDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  // Identificador único del mensaje

    private long from;  // El ID del usuario que envía el mensaje

    private LocalDateTime timestamp;  // Fecha y hora de envío del mensaje

    private String content;  // El contenido del mensaje (puede ser texto o la URL de una imagen)
    
    @Lob
    private String base54Data;
    
       @ManyToOne
    @JoinColumn(name = "room_id", nullable = false) // Clave foránea que enlaza con Room
    private Room room;

    @Enumerated(EnumType.STRING)
    private MessageType type;  // Tipo de mensaje (texto o imagen)

   
    // Constructor vacío requerido por JPA
    public Message() {}

   

    // Método para crear el mensaje a partir de un DTO
    public Message(SendMessageDTO messageDTO) {
        this.from = messageDTO.getFrom();
           this.timestamp = LocalDateTime.now();  // La fecha y hora se establece automáticamente
        this.content = messageDTO.getContent();
        this.base54Data = messageDTO.getBase54Data();
        this.type = messageDTO.getType();
     
   
    }

    // Enum que define los tipos de mensajes: texto o imagen
    public enum MessageType {
        TEXT, IMAGE
    }
}
