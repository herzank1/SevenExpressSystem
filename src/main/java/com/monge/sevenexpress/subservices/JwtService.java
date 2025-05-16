
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.JwtToken;
import com.monge.sevenexpress.entities.JwtToken.TokenStatus;
import com.monge.sevenexpress.entities.User.Role;
import com.monge.sevenexpress.repositories.JwtTokenRepository;
import io.jsonwebtoken.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class JwtService implements ServiceCacheable<JwtToken, String> {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // Expiración del token (10 horas)

    // Caché de negocios indexado por ID
    private final Map<String, JwtToken> tokensCache = new ConcurrentHashMap<>();

    @Autowired
    private JwtTokenRepository jwtTokenRepository;  // Repositorio para interactuar con la base de datos

    public String generateToken(String username, Role role) {
        String token = Jwts.builder()
                .subject(username)
                .claim("role", role.name()) // Usa `claim` en lugar de `claims().add()`
                .issuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Usar `setExpiration` en lugar de `expiration`
                .signWith(key) // Especifica el key para firmar el token
                .compact();  // Devuelve el JWT generado

        // Guardar el token en la base de datos
        JwtToken jwtToken = new JwtToken(token, username, new Date(System.currentTimeMillis() + EXPIRATION_TIME), TokenStatus.ACTIVADO);

        saveToken(jwtToken);
        return token;
    }

    private JwtToken saveToken(JwtToken token) {
        token = jwtTokenRepository.save(token);
        if (token != null) {
            cacheEntity(token.getToken(), token);
            return token;

        }
        return null;
    }

    /**
     * *
     * obtener token del cache o de la base de datos
     *
     * @param token
     * @return
     */
    private JwtToken getToken(String token) {
        
         if (getCache().containsKey(token)) {
            return getCache().get(token);
        }
                
        JwtToken jToken = jwtTokenRepository.findByToken(token).orElse(null);
       
        if (jToken != null) {
            cacheEntity(jToken.getToken(), jToken);
        }

        return jToken;
    }

    // Método para validar si el token es válido
    public boolean validateToken(String token) {
        // Verificar si el token está en la base de datos y es válido
        JwtToken jwtToken = getToken(token);

        if (jwtToken == null) {
            return false;  // Token no encontrado o revocado o expirado
        }

        if (jwtToken.getStatus() != TokenStatus.ACTIVADO) {
            return false;

        }

        if (jwtToken.getExpirationDate().before(new Date())) {
            jwtToken.setStatus(TokenStatus.EXPIRADO);
            saveToken(jwtToken);
            return false;
        }

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);  // Esto lanzará un error si el token no es válido
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // El token es inválido
        }
    }

    // Extraer el username desde el token
    public String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

        return claims.getSubject();  // Devuelve el 'subject' del JWT
    }

    // Revocar un token
    public void revokeToken(String token) {
        JwtToken jwtToken = getToken(token);
        jwtToken.setStatus(TokenStatus.REVOCADO);

        saveToken(jwtToken);
    }

    @Override
    public Map<String, JwtToken> getCache() {
        return tokensCache;
    }
    
    public void evictAllByUsernameFromCache(String username) {
    tokensCache.entrySet().removeIf(entry ->
        entry.getValue() != null && username.equals(entry.getValue().getUsername())
    );
}


}
