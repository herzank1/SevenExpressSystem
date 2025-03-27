/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.entities.BalanceAccount;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.repositories.DeliveryRepository;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class DeliveryService implements ServiceCacheable<Delivery, Long> {

    @Autowired
    private final UserService userService;

    @Autowired
    private final DeliveryRepository deliveryRepository;

    @Autowired
    private final BalanceAccountService balanceAccountService;

    // Cache en memoria para almacenar los objetos Delivery
    private final Map<Long, Delivery> deliveriesCache = new ConcurrentHashMap<>();

//    @Autowired
//    public DeliveryService(UserService userService, DeliveryRepository deliveryRepository, BalanceAccountService balanceAccountService) {
//        this.userService = userService;
//        this.deliveryRepository = deliveryRepository;
//        this.balanceAccountService = balanceAccountService;
//    }

    /**
     * Obtiene o crea un BalanceAccount asociado al Business.
     */
    @Transactional
    public BalanceAccount getBalanceAccount(Delivery delivery) {
        
        BalanceAccount balanceAccount = delivery.getBalanceAccount();
        
         if (balanceAccount == null) {
            balanceAccount = balanceAccountService.getBalanceAccount(delivery);
            delivery.setBalanceAccount(balanceAccount);
            deliveryRepository.save(delivery);
        }

        return balanceAccount;
       
    }

    public Delivery getById(long id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        Delivery delivery = deliveryRepository.findById(id).orElse(null);
        if (delivery != null) {
            cacheEntity(delivery.getId(), delivery);
        }
        return delivery;
    }

    private Delivery getOrSetUserNameOf(Delivery delivery) {
        User user = userService.findByAccountId(delivery.getId());
        if (user != null) {
            delivery.setUserName(user.getUsername());
        }

        return delivery;

    }

    /**
     * Busca un usuario por nombre de usuario, primero en caché.
     */
    public Delivery getByUserName(String username) {
        // Buscar en caché iterando sobre el Map
        for (Delivery delivery : deliveriesCache.values()) {
            if (delivery.getUserName().equals(username)) {
                return delivery;
            }
        }

        // Buscar en BD si no está en caché
        User user = userService.findByUserName(username);
        if (user != null) {
            Delivery findById = deliveryRepository.findById(user.getAccountId()).orElse(null);

            if (findById != null) {
                findById.setUserName(username);
                cacheEntity(findById.getId(), findById);
                return findById;
            }

        }
        return null;
    }

 

    public ArrayList<Delivery> getAllDeliveries() {
        return new ArrayList<>(deliveriesCache.values());
    }

    public ArrayList<Delivery> getConectedDeliveries() {
        return getAllDeliveries()
                .stream()
                .filter(c -> c.isConected())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Guarda un Delivery y actualiza la caché.
     */
    public Delivery save(Delivery delivery) {
        // Guardar el delivery en la base de datos
        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Asignar el userName si es nulo
        if (savedDelivery.getUserName() == null) {
            getOrSetUserNameOf(delivery);
        }

        // Verificar si el userName es válido antes de agregar al cache
        if (savedDelivery.getUserName() != null && savedDelivery != null) {
            cacheEntity(savedDelivery.getId(), savedDelivery);
        } else {
            // Manejar el caso en que el userName no se asigna correctamente
            System.out.println("No se pudo asignar el userName para el delivery con ID: " + delivery.getId());
            // O lanzar una excepción si es crítico
        }

        return savedDelivery;
    }

    @Override
    public Map<Long, Delivery> getCache() {
        return deliveriesCache;
    }

}
