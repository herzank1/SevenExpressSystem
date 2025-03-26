/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.security;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.services.JwtService;
import com.monge.sevenexpress.services.TokenBlacklistService;
import com.monge.sevenexpress.services.UserService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService,TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/business/")
                ||path.startsWith("/customers/")
                ||path.startsWith("/deliveries/")
                ||path.startsWith("/admins/");
    }

@Override
protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
    
    logger.info("Ejecutando filtro JWT...");

    String token = getJwtFromRequest(request);
    
     // Verificar si el token está en la blacklist
        if (token != null && tokenBlacklistService.isTokenBlacklisted(token)) {
            logger.warn("Token revocado detectado.");
          
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(ApiResponse.error("Token inválido o revocado").toJson());
            return;
        }

    // Verificamos si el token existe y es válido
    if (token != null && jwtService.validateToken(token)) {
        String username = jwtService.extractUsername(token);

        // Aquí obtienes el usuario (Business o cualquier otro rol que tengas)
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (userDetails != null) {
            // Log para verificar si el UserDetails ha sido correctamente cargado
            logger.info("UserDetails cargado para el usuario: {}", username);

            // Creando la autenticación
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // Asignando la autenticación al contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Log para verificar si la autenticación se ha establecido correctamente
            logger.info("Autenticación establecida para el usuario: {}", username);
        } else {
            // Log para verificar si no se pudo cargar el usuario
            logger.warn("No se pudo cargar el UserDetails para el usuario: {}", username);
        }
    } else {
        // Log si el token es nulo o inválido
        logger.warn("Token inválido o nulo detectado.");
    }

    // Continuar con el filtro
    filterChain.doFilter(request, response);
}



    private String getJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
