/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;

import com.monge.sevenexpress.repositories.UserRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class UserService implements UserDetailsService, ServiceCacheable<User, Long> {

    @Autowired
    private UserRepository userRepository;

    // Caché de usuarios indexado por username
    private final Map<Long, User> usersCache = new ConcurrentHashMap<>();

    public boolean userExistByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    /**
     * Busca un usuario por su id.
     */
    public User findById(long id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            cacheEntity(user.getId(), user);
        }
        return user;
    }

    /**
     * Busca un usuario por nombre de usuario, primero en caché.
     */
    public User findByUserName(String name) {
        // Buscar en caché iterando sobre el Map
        for (User user : getCache().values()) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }

        // Buscar en BD si no está en caché
        User user = userRepository.findByUserName(name).orElse(null);
        if (user != null) {
            cacheEntity(user.getId(), user);
        }
        return user;
    }

    /**
     * Busca un usuario por `accountId`, iterando sobre el caché.
     */
    public User findByAccountId(Long accountId) {
        // Buscar en caché iterando sobre el Map
        for (User user : getCache().values()) {
            if (user.getAccountId().equals(accountId)) {
                return user;
            }
        }

        // Buscar en BD si no está en caché
        User user = userRepository.findByAccountId(accountId).orElse(null);
        if (user != null) {
            cacheEntity(user.getId(), user);
        }
        return user;
    }

    /**
     * Guarda un usuario en la base de datos y actualiza la caché.
     */
    public User save(User user) {
        User savedUser = userRepository.save(user);
        if (savedUser != null) {
            cacheEntity(savedUser.getId(), savedUser);
        }
        return savedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    }

    public String getUserNameOf(Object entity) {
        Long id = null;

        if (entity instanceof Admin) {
            id = ((Admin) entity).getId();
        } else if (entity instanceof Business) {
            id = ((Business) entity).getId();
        } else if (entity instanceof Delivery) {
            id = ((Delivery) entity).getId();
        }

        if (id != null) {
            User user = userRepository.findByAccountId(id).orElse(null);
            return (user != null) ? user.getUsername() : null;
        }

        return null;
    }

    @Override
    public Map<Long, User> getCache() {
        return usersCache;
    }

}
