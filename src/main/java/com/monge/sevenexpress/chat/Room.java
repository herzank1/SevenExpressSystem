/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.chat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  // La clave primaria de tipo UUID

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();  // Inicialización de la lista de mensajes

    // Constructor
    public Room(UUID id) {
        this.id = id;
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

}
