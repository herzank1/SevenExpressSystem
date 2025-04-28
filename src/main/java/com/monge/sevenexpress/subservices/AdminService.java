/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.repositories.AdminRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DeliveryExpress
 */
@Service
@Data
public class AdminService implements ServiceCacheable<Admin, String> {

    @Autowired
    private AdminRepository adminRepository;

    // Cache en memoria para almacenar los objetos Admins
    private final Map<String, Admin> adminsCache = new ConcurrentHashMap<>();

 


    public Admin getById(String id) {

        // Buscar en caché
        if (getCache().containsKey(id)) {
            return getCache().get(id);
        }

        // Buscar en BD si no está en caché
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            cacheEntity(admin.getId(), admin);
        }
        return admin;

    }



    public Admin save(Admin admin) {
        
         if (admin.getId() == null || admin.getId().isEmpty()) {
            String generatedId =  generateId();
            admin.setId(generatedId);
        }

        Admin savedAdmin = adminRepository.save(admin);

        // Verificar si el userName es válido antes de agregar al cache
        if (savedAdmin != null) {
            getCache().put(savedAdmin.getId(), savedAdmin);
        }

        return savedAdmin;

    }

    @Override
    public Map<String, Admin> getCache() {
        return adminsCache;
    }
    
     private String generateId() {
        return "A-" + adminRepository.count();
    }

}
