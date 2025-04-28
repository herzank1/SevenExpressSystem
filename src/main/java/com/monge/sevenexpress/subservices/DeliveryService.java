/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.repositories.DeliveryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class DeliveryService implements ServiceCacheable<Delivery, String> {

    @Autowired
    private final DeliveryRepository deliveryRepository;

    // Cache en memoria para almacenar los objetos Delivery
    private final Map<String, Delivery> deliveriesCache = new ConcurrentHashMap<>();


    public Delivery getById(String id) {
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
        
         if (delivery.getId() == null || delivery.getId().isEmpty()) {
            String generatedId =  generateId();
            delivery.setId(generatedId);
        }
        
        
        // Guardar el delivery en la base de datos
        Delivery savedDelivery = deliveryRepository.save(delivery);


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
    public Map<String, Delivery> getCache() {
        return deliveriesCache;
    }
    
   @Override
public void clearCache() {
    // Obtener todos los `Delivery` desde la base de datos
    List<Delivery> dbDeliveries = deliveryRepository.findAll();

    // Crear un nuevo caché sin los elementos `conected = false`
    Map<String, Delivery> newCache = new ConcurrentHashMap<>();

    for (Delivery dbDelivery : dbDeliveries) {
        // Si existe en el caché, mantener sus valores `@Transient`
        Delivery cachedDelivery = deliveriesCache.get(dbDelivery.getId());

        if (cachedDelivery != null) {
            dbDelivery.setPosition(cachedDelivery.getPosition());
            dbDelivery.setConected(cachedDelivery.isConected());
            dbDelivery.setUserName(cachedDelivery.getUserName());
            dbDelivery.setLastOrderAsignedTimeStamp(cachedDelivery.getLastOrderAsignedTimeStamp());
        }

        // Agregar solo si `conected == true`
        if (dbDelivery.isConected()) {
            newCache.put(dbDelivery.getId(), dbDelivery);
        }
    }

    // Reemplazar el caché antiguo
    deliveriesCache.clear();
    deliveriesCache.putAll(newCache);

    System.out.println("🧹 Caché limpiado y actualizado con " + deliveriesCache.size() + " elementos.");
}

  private String generateId() {
        return "D-" + deliveryRepository.count();
    }


}
