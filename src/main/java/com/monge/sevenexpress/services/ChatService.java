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

    // Obtener mensajes desde una fecha específica (si 'from' es null, se retornan todos los mensajes)
    public List<Message> getMessagesFrom(UUID roomId, LocalDateTime fromDate) {

        return getOrCreateRoom(roomId).getMessagesFrom(fromDate);
//        
//        if (fromDate != null) {
//            return messageRepository.findByRoomIdAndTimestampAfter(roomId, fromDate);
//        } else {
//            return messageRepository.findByRoomId(roomId); // Si no se pasa 'from', retornamos todos los mensajes
//        }
    }

    @EventListener
    public void executePostOrderDelivered(OnOrderDeliveredEvent event) {
        Room room = getOrCreateRoom(event.getOrder().getId());

        if (!room.getMessages().isEmpty()) {
            messageRepository.saveAll(room.getMessages());
        }
        roomRepository.save(room);
    }

    @Override
    public Map<UUID, Room> getCache() {
        return rooms;
    }

}
