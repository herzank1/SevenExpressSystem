/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.entities.dto;

import com.monge.sevenexpress.chat.Message;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class SendMessageDTO {

    private UUID roomId;

    private String from;
    private LocalDateTime timestamp; // Fecha y hora de la transacción
    private String content;
    private Message.MessageType type;

}
