/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.subservices;

import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
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
public class CustomerService implements ServiceCacheable<Customer, Long>{
    
    @Autowired
    private CustomerRepository customerRepository;
    
      // Caché de clientes indexado por número de teléfono
    private final Map<Long, Customer> customerCache = new ConcurrentHashMap<>();
    
   /**
     * Busca un cliente por número de teléfono, usando caché para mejorar rendimiento.
     */
   public Customer findByPhoneNumber(String customerPhone) {
    // Buscar en caché primero
    for (Customer cachedCustomer : customerCache.values()) {
        if (cachedCustomer.getPhoneNumber().equals(customerPhone)) {
            return cachedCustomer;
        }
    }

    // Buscar en base de datos si no está en caché
    Customer customer = customerRepository.findByPhoneNumber(customerPhone).orElse(null);
    if (customer != null) {
        // Cachear el cliente si se encuentra en la base de datos
        cacheEntity(customer.getId(),customer);
    }

    return customer;
}
    
       /**
     * Guarda un cliente en la base de datos y actualiza la caché.
     */
    @Transactional
    public Customer save(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        if (savedCustomer != null) {
            cacheEntity(savedCustomer.getId(),customer);
        }
        return savedCustomer;
    }
    
      @Transactional
    public Customer merge(Customer customer) {
        // Verificar si el cliente existe por teléfono
        Optional<Customer> existingCustomerOpt = customerRepository.findByPhoneNumber(customer.getPhoneNumber());

        if (existingCustomerOpt.isPresent()) {
            // Si existe, obtenemos el cliente
            Customer existingCustomer = existingCustomerOpt.get();

            // Actualizamos los campos necesarios
            existingCustomer.setName(customer.getName());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setPosition(customer.getPosition());
            
            //

            // Realizamos el merge, es decir, actualizamos los datos de la entidad
             Customer saveCustomer = save(existingCustomer); // Esto sincroniza la entidad con la base de datos
        cacheEntity(saveCustomer.getId(),customer);
        return saveCustomer;
        
        } else {
            // Si no existe, guardamos el nuevo cliente
            return customerRepository.save(customer);
        }
    }

   
 

    @Override
    public Map<Long, Customer> getCache() {
    return customerCache;
    }
    
}
