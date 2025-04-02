/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.chat.Message;
import com.monge.sevenexpress.chat.Room;
import com.monge.sevenexpress.events.OnOrderDeliveredEvent;
import com.monge.sevenexpress.repositories.MessageRepository;
import com.monge.sevenexpress.repositories.RoomRepository;
import com.monge.sevenexpress.subservices.ServiceCacheable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

@Service
public class ChatService implements ServiceCacheable<Room, UUID> {

    private final ConcurrentHashMap<UUID, Room> rooms = new ConcurrentHashMap<>();

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Room getOrCreateRoom(UUID roomId) {
        return rooms.computeIfAbsent(roomId, id -> new Room(id));
    }

    public void saveMessage(UUID roomId, Message message) {
        Room room = getOrCreateRoom(roomId);
        room.addMessage(message);
    }

    public List<Message> getChatUpdates(UUID roomId, long user) {
        List<Message> updatedMessages = new ArrayList<>();

        // Obtén o crea la sala de chat
        Room room = getOrCreateRoom(roomId);

        // Iterar sobre los mensajes de la sala
        for (Message message : room.getMessages()) {
            // Verificar si el mensaje ha sido enviado por el usuario y si no ha sido marcado como 'true'
            if (message.getSeentBy().get(user) == null) {
                // Cambiar el valor del mapa para el usuario a 'true'
                message.getSeentBy().put(user, false);

            }

            if (!message.getSeentBy().get(user)) {
                updatedMessages.add(message);
                message.getSeentBy().put(user, true);
            }

        }

        // Retornar la lista de mensajes actualizados
        return updatedMessages;
    }

    /*el cliente debera llama a esta funcion cuando inice el chat*/
    public List<Message> getChat(UUID roomId) {

        return getOrCreateRoom(roomId).getMessages();

    }

    @EventListener
    public void executePostOrderDelivered(OnOrderDeliveredEvent event) {
        Room room = getOrCreateRoom(event.getOrder().getId());

        if (!room.getMessages().isEmpty()) {
            messageRepository.saveAll(room.getMessages());
        }
        roomRepository.save(room);
        
        rooms.remove(event.getOrder().getId());
    }

    @Override
    public Map<UUID, Room> getCache() {
        return rooms;
    }

}
