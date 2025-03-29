/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.chat.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

     // Obtener mensajes de una sala específica a partir de una fecha
    List<Message> findByRoom_IdAndTimestampAfter(UUID roomId, LocalDateTime fromDate);
    
    // Obtener todos los mensajes de una sala específica
    List<Message> findByRoom_Id(UUID roomId);

}
