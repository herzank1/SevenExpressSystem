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

    //  @Autowired
    //  private MessageRepository messageRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Crea o devuelve la sala desde caché
    public Room getOrCreateRoom(UUID roomId) {
        return rooms.computeIfAbsent(roomId, id -> new Room(id));
    }

    // Agrega un mensaje a la sala en caché (solo en memoria)
    public void saveMessage(UUID roomId, Message message) {
        Room room = getOrCreateRoom(roomId);
        synchronized (room) { // sincroniza por Room individual
            message.setRoom(room); // importante: asignar la relación inversa
            room.addMessage(message);
        }
    }

    // Mensajes actualizados para el usuario
    public List<Message> getChatUpdates(UUID roomId, String accountId) {
        Room room = getOrCreateRoom(roomId);
        List<Message> updatedMessages = new ArrayList<>();

        synchronized (room) {
            for (Message message : room.getMessages()) {
                if (message.getSeentBy().get(accountId) == null) {
                    message.getSeentBy().put(accountId, false);
                }

                if (!message.getSeentBy().get(accountId)) {
                    updatedMessages.add(message);
                    message.getSeentBy().put(accountId, true);
                }
            }
        }

        return updatedMessages;
    }

    // Devuelve todos los mensajes en memoria
    public List<Message> getChat(UUID roomId) {
        return getOrCreateRoom(roomId).getMessages();
    }

    // Evento que guarda todo y elimina de caché (protegido contra concurrencia)
    @EventListener
    public void executePostOrderDelivered(OnOrderDeliveredEvent event) {

        UUID roomId = event.getOrder().getId();
        System.out.print("executePostOrderDelivered: "+roomId.toString());

        Room cachedRoom = getOrCreateRoom(roomId);
        

        try {
            // Sincronizar por Room individual para evitar condiciones de carrera
            synchronized (cachedRoom) {
                // Recargar o crear nueva instancia desde base de datos
                Room roomFromDB = roomRepository.findById(roomId).orElse(cachedRoom);

                // Asegura que cada mensaje tenga la referencia al Room correcta
                for (Message msg : cachedRoom.getMessages()) {
                    msg.setRoom(roomFromDB);
                }


                // Guardar Room y cascadea los mensajes
                roomRepository.save(roomFromDB);

                // Eliminar de la caché
                removeFromCache(roomId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<UUID, Room> getCache() {
        return rooms;
    }

}
