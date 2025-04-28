/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.repositories.BusinessRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class BusinessService implements ServiceCacheable<Business, String> {

    @Autowired
    private final BusinessRepository businessRepository;

    @Autowired
    private final BusinessContractService businessContractService;

    // Caché de negocios indexado por ID
    private final Map<String, Business> businessCache = new ConcurrentHashMap<>();

    /**
     * Obtiene un Business por su ID, primero en caché.
     */
    public Business getById(String id) {
        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        Business business = businessRepository.findById(id).orElse(null);
        if (business != null) {
            cacheEntity(business.getId(), business);
        }
        return business;
    }

    /**
     * Guarda un Business en la base de datos y lo almacena en caché.
     */
    public Business save(Business business) {
        if (business.getId() == null || business.getId().isEmpty()) {
            String generatedId =  generateId();
            business.setId(generatedId);
        }

        Business savedBusiness = businessRepository.save(business);
        if (savedBusiness != null) {
            cacheEntity(savedBusiness.getId(), savedBusiness);
        }
        return savedBusiness;
    }

    @Override
    public Map<String, Business> getCache() {
        return businessCache;
    }

    private String generateId() {
        return "B-" + businessRepository.count();
    }

}
