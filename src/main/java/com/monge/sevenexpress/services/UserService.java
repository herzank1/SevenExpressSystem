/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.entities.User.Role;
import com.monge.sevenexpress.events.OnPaymentReceivedFromBusiness;

import com.monge.sevenexpress.repositories.UserRepository;
import com.monge.sevenexpress.subservices.AdminService;
import com.monge.sevenexpress.subservices.BusinessService;
import com.monge.sevenexpress.subservices.CustomerService;
import com.monge.sevenexpress.subservices.DeliveryService;
import com.monge.sevenexpress.subservices.ServiceCacheable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private BusinessService businessService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminService adminService;

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

    public <T> ResponseEntity<ApiResponse> updateEntity(T entity, Role accountType) {
        switch (accountType) {
            case Role.CUSTOMER:
                return updateUser((User) entity);
            case Role.DELIVERY:
                return updateDelivery((Delivery) entity);
            case Role.BUSINESS:
                return updateBusiness((Business) entity);
            case Role.ADMIN:
                return updateAdmin((Admin) entity);
            default:
                return updateUser((User) entity);
        }
    }

    private ResponseEntity<ApiResponse> updateUser(User user) {
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully.", user));
    }

    private ResponseEntity<ApiResponse> updateDelivery(Delivery delivery) {
        deliveryService.save(delivery);
        return ResponseEntity.ok(ApiResponse.success("Delivery updated successfully.", delivery));
    }

    private ResponseEntity<ApiResponse> updateBusiness(Business business) {
        businessService.save(business);
        return ResponseEntity.ok(ApiResponse.success("Business updated successfully.", business));
    }

    private ResponseEntity<ApiResponse> updateCustomer(Customer customer) {
        customerService.save(customer);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully.", customer));
    }

    private ResponseEntity<ApiResponse> updateAdmin(Admin admin) {
        adminService.save(admin);
        return ResponseEntity.ok(ApiResponse.success("Admin updated successfully.", admin));
    }

    public <T> ResponseEntity<ApiResponse> getEntityList(String entityName) {
        switch (entityName) {
            case "User":
                return getUserList();
            case "Delivery":
                return getDeliveryList();
            case "Business":
                return getBusinessList();
            case "Customer":
                return getCustomerList();
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Unknown entity"));
        }
    }

    private ResponseEntity<ApiResponse> getUserList() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("User list fetched successfully.", users));
    }

    private ResponseEntity<ApiResponse> getDeliveryList() {
        List<Delivery> deliveries = deliveryService.getDeliveryRepository().findAll();
        return ResponseEntity.ok(ApiResponse.success("Delivery list fetched successfully.", deliveries));
    }

    private ResponseEntity<ApiResponse> getBusinessList() {
        List<Business> businesses = businessService.getBusinessRepository().findAll();
        return ResponseEntity.ok(ApiResponse.success("Business list fetched successfully.", businesses));
    }

    private ResponseEntity<ApiResponse> getCustomerList() {
        List<Customer> customers = customerService.getCustomerRepository().findAll();
        return ResponseEntity.ok(ApiResponse.success("Customer list fetched successfully.", customers));
    }

    public Delivery getDeliveryByUserName(String userName) {

        User user = findByUserName(userName);
        if (user == null) {
            return null;
        }

        Delivery byId = deliveryService.getById(user.getAccountId());
        byId.setUserName(userName);

        return byId;

    }

    public Admin getAdminByUserName(String userName) {

        User user = findByUserName(userName);
        if (user == null) {
            return null;
        }

        return adminService.getById(user.getAccountId());

    }

    public Business getBusinessByUserName(String userName) {
        User user = findByUserName(userName);
        if (user == null) {
            return null;
        }

        return businessService.getById(user.getAccountId());

    }

    @Scheduled(fixedRate = 3600000) // Ejecuta cada 1 hora (en milisegundos)
    public void cacheCleaner() {
        long now = System.currentTimeMillis();
        clearCache();
        businessService.clearCache();
        customerService.clearCache();
        adminService.clearCache();
        deliveryService.clearCache();

    }

}
