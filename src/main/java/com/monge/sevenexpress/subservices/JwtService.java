/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.User.Role;
import io.jsonwebtoken.*;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // Expiración del token (10 horas)


 public String generateToken(String username, Role role) {
    return Jwts.builder()
            .subject(username)
            .claim("role", role.name())  // Usa `claim` en lugar de `claims().add()`
            .issuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Usar `setExpiration` en lugar de `expiration`
            .signWith(key)  // Especifica el key para firmar el token
            .compact();  // Devuelve el JWT generado
}

    
     // Método para validar si el token es válido
    public boolean validateToken(String token) {
          try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);  // Esto lanzará un error si el token no es válido
            return true; // Si no se lanza ninguna excepción, el token es válido
        } catch (JwtException | IllegalArgumentException e) {
            // Si el token es inválido o ha expirado, atrapamos la excepción
            return false;
        }
    }


     // Extraer el username desde el token
    public String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

        return claims.getSubject();  // Devuelve el 'subject' del JWT
    }

    // Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parser()  // Usamos parserBuilder() ahora
                                  .setSigningKey(key)
                                  .build()
                                  .parseClaimsJws(token)  // Usamos parseClaimsJwt() en lugar de parseClaimsJws
                                  .getBody()
                                  .getExpiration();
        return expirationDate.before(new Date());  // Si la fecha de expiración es antes de la fecha actual, el token ha expirado
    }

   

}

