
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.chat.Message;
import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.entities.dto.SendMessageDTO;
import com.monge.sevenexpress.services.ChatService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego Villarreal
 * Controlador para manejar los chat de las ordenes
 * el Chat id es el Order id.
 * 
 */
@RestController
@RequestMapping("/api/v1")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /***
     * Recibe un mensaje de un cliente y lo ingresa al chat
     * @param message de typo SendMessageDTO
     * @return un success ApiResponse si el mensaje se recibio exitosamente
     */
    @PostMapping("/chat/sendMessage") 
    public ResponseEntity<ApiResponse> sendMessage(@RequestBody SendMessageDTO message) {

        try {

            chatService.saveMessage(message.getRoomId(), new Message(message));
            return ResponseEntity.ok(ApiResponse.success("message received!", message));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }

    }

     /***
     * Endpoint para obtener todos los mensajes  de un chat
     * @param roomId
     * @return un ApiResponse con un list de Message
     */
    @GetMapping("/chat/getChat")
    public ResponseEntity<ApiResponse> getChat(@RequestParam UUID roomId) {
        try {

            // Llamamos al servicio para obtener los mensajes desde la fecha proporcionada (o todos los mensajes si 'from' es null)
            List<Message> messages = chatService.getChat(roomId);
            return ResponseEntity.ok(ApiResponse.success("room", messages));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }

    }

    /***
     * Endpoint para obtener actualizaciones del un chat
     * @param roomId
     * @param accountId
     * @return un ApiResponse con un list de Message
     */
    @GetMapping("/chat/getChatUpdates")
    public ResponseEntity<ApiResponse> getChatUpdatesMessages(@RequestParam UUID roomId, @RequestParam String accountId) {
        try {

            // Llamamos al servicio para obtener los mensajes desde la fecha proporcionada (o todos los mensajes si 'from' es null)
            List<Message> messages = chatService.getChatUpdates(roomId, accountId);

            return ResponseEntity.ok(ApiResponse.success("room", messages));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }

    }

}
