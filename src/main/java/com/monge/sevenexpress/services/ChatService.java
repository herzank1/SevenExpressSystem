/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.chat.Message;
import com.monge.sevenexpress.chat.Room;
import com.monge.sevenexpress.events.OnOrderCanceledEvent;
import com.monge.sevenexpress.events.OnOrderDeliveredEvent;
import com.monge.sevenexpress.repositories.RoomRepository;
import com.monge.sevenexpress.subservices.ServiceCacheable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

/**
 * *
 * Este servicio maneja la logica de un chat asi mismo se puede usar como un
 * sistema de notificacion el map room contiene todos los chats/rooms en el
 * momento, la llave puede ser un Order.id o cualquier indentidicador de un
 * objeto el cliente es el encargado de manejar la logica necesaria para acceder
 * al chat e interactual con ella, ya sea como un chat o como una bandeja de
 * entrada
 *
 * @author Diego Villarreal
 */
@Service
public class ChatService implements ServiceCacheable<Room, String> {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    @Autowired
    private RoomRepository roomRepository;

    // Crea o devuelve la sala, buscando en caché y luego en base de datos
    public Room getOrCreateRoom(String roomId) {
        return rooms.computeIfAbsent(roomId, id -> {
            Room roomFromDb = roomRepository.findById(id).orElse(null);
            return (roomFromDb != null) ? roomFromDb : new Room(id);
        });
    }

    // Agrega un mensaje a la sala y guarda en la base de datos
    public void saveMessage(String roomId, Message message) {
        Room room = getOrCreateRoom(roomId);
        synchronized (room) { // sincroniza por Room individual
            message.setRoom(room); // importante: asignar la relación inversa
            room.addMessage(message);

            try {
                // Guarda room actualizado en la base de datos
                Room roomFromDB = roomRepository.findById(roomId).orElse(room);
                roomFromDB.getMessages().add(message);
                roomRepository.save(roomFromDB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Mensajes actualizados para el usuario
    public List<Message> getChatUpdates(String roomId, String accountId) {
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

    // Devuelve todos los mensajes, primero desde la caché, si no, de la base de datos
    public List<Message> getChat(String roomId) {
        Room room = rooms.get(roomId);

        if (room == null) {
            // Si no está en caché, buscar en base de datos
            room = roomRepository.findById(roomId).orElse(null);
            if (room != null) {
                rooms.put(roomId, room); // Actualizar caché
            } else {
                // Si tampoco existe en la base de datos, crear uno nuevo vacío
                room = new Room(roomId);
                rooms.put(roomId, room);
            }
        }

        return room.getMessages();
    }


    @Override
    public Map<String, Room> getCache() {
        return rooms;
    }
    
    /*escuchar algunos eventos para notificar*/
     @EventListener
     void executePostOrderDelivered(OnOrderDeliveredEvent event) {
         sendMessage(event.getOrder().getId().toString(),event.getOrder().getTitle()+": Entregada!");
         sendMessage(Channels.ORDENES_GENERAL,event.getOrder().getTitle()+": Entregada!");
    
    }
    
      @EventListener
     void executePostOrderCanceled(OnOrderCanceledEvent event) {
         try{
         sendMessage(event.getOrder().getId().toString(),event.getOrder().getTitle()+": Cancelada por "+event.getCanceler()+", razon: "+event.getReason());
         sendMessage(Channels.ORDENES_GENERAL,event.getOrder().getTitle()+": Cancelada por "+event.getCanceler()+", razon: "+event.getReason());
         }catch(Exception e){
         e.printStackTrace();
         }
          
    
    }

    /**
     * *
     *
     * @param roomId
     * @param text
     */
    public void sendMessage(String roomId, String text) {
        Message message = new Message();
        message.setFrom("Delivery Express");
        message.setContent(text);
        saveMessage(roomId, message);
    }

    public static interface Channels {

        String GENERAL = "GENERAL";
        String PAGOS = "PAGOS";
        String ORDENES_GENERAL = "ORDENES_GENERAL";
        String REPORTES = "REPORTES";

    }

}
