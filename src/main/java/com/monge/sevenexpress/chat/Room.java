/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.chat;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
public class Room {

    @Id
    private String id;  // La clave primaria de tipo UUID

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();  // Inicialización de la lista de mensajes

    private LocalDateTime createdAt;  // Fecha de creación del Room

    @Enumerated(EnumType.STRING)
    private RoomType roomType = RoomType.GROUP;  // Tipo de Room: default GROUP

    // Constructor con id
    public Room(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();  // Inicializa la fecha cuando se crea
        this.roomType = RoomType.GROUP;        // Por default es un GROUP
    }

    // Constructor vacío (requerido por JPA)
    public Room() {
        this.createdAt = LocalDateTime.now();
        this.roomType = RoomType.GROUP;
    }

    // Método para agregar un mensaje
    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getMessagesFrom(LocalDateTime fromDate) {

        if (fromDate != null) {
            // Filtrar los mensajes después de la fecha proporcionada
            return messages.stream()
                    .filter(message -> message.getTimestamp().isAfter(fromDate))
                    .collect(Collectors.toList());
        } else {
            // Si no hay 'fromDate', retornar todos los mensajes
            return messages;
        }
    }

    // Enum para RoomType
    public enum RoomType {
        GROUP,
        CHANNEL
    }

}
