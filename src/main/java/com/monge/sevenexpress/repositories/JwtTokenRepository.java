/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.JwtToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DeliveryExpress
 */
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    Optional<JwtToken> findByToken(String token);  // Buscar token por su valor

    @Transactional
    @Modifying
    void deleteAllByUsername(String username);

    Optional<JwtToken> findFirstByUsername(String username);

    // Buscar todos los tokens por nombre de usuario
    List<JwtToken> findAllByUsername(String username);

    // Eliminar todos los tokens con estado EXPIRADO
    @Transactional
    @Modifying
    @Query("DELETE FROM JwtToken t WHERE t.status = 'EXPIRADO'")
    void deleteAllExpiredTokens();

    // Eliminar todos los tokens con estado REVOCADO
    @Transactional
    @Modifying
    @Query("DELETE FROM JwtToken t WHERE t.status = 'REVOCADO'")
    void deleteAllRevokedTokens();
}
