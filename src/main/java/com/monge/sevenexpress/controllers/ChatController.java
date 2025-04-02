/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.controllers;

import com.monge.sevenexpress.chat.Message;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.entities.dto.SendMessageDTO;
import com.monge.sevenexpress.services.ChatService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/chat") // Prefijo para todas las rutas
public class ChatController {
    
   @Autowired
    private  ChatService chatService;
   
    @PostMapping("/sendMessage") // Cliente envía mensaje a /app/chat/{roomId}
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody SendMessageDTO message) {
        chatService.saveMessage(message.getRoomId(), new Message(message));
         return ResponseEntity.ok(ApiResponse.success("message received!",message ));
    
    }
    
    @GetMapping("/getChat")
    public ResponseEntity<ApiResponse> getRoomMessages(@RequestParam UUID roomId) {

   // Llamamos al servicio para obtener los mensajes desde la fecha proporcionada (o todos los mensajes si 'from' es null)
    List<Message> messages = chatService.getChat(roomId);    
    
    
        return ResponseEntity.ok(ApiResponse.success("room", messages));
    }
    
       @GetMapping("/getChatUpdates")
    public ResponseEntity<ApiResponse> getRoomUpdatesMessages(@RequestParam UUID roomId,@RequestParam long user) {

   // Llamamos al servicio para obtener los mensajes desde la fecha proporcionada (o todos los mensajes si 'from' es null)
    List<Message> messages = chatService.getChatUpdates(roomId,user);    
    
    
        return ResponseEntity.ok(ApiResponse.success("room", messages));
    }
}
