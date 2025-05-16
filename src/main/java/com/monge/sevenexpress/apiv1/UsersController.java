/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.DeliveryUpdateLocationRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.entities.User.Role;
import com.monge.sevenexpress.entities.UserProfile;
import com.monge.sevenexpress.entities.UserProfile.AccountStatus;
import com.monge.sevenexpress.entities.dto.UserDTO;
import com.monge.sevenexpress.entities.dto.UserProfileDTO;
import com.monge.sevenexpress.services.OrdersControlService;
import com.monge.sevenexpress.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1") // Prefijo para todas las rutas
public class UsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrdersControlService ordersControlService;

    @Autowired
    AuthController authController;

    @GetMapping("/users/deliveries")
    public ResponseEntity<ApiResponse> getDeliveries() {
        return ResponseEntity.ok(ApiResponse.success("sucess", ordersControlService.getConectedDeliveries()));

    }

    @PostMapping("/deliveries/switchConnection")
    public ResponseEntity<ApiResponse> switchDeliveryConnectionStatus() {
        try {
            Delivery delivery = authController.getAuthenticatedDelivery();
            delivery.setConected(!delivery.isConected());
            return ResponseEntity.ok(ApiResponse.success("Aceptación de pedidos...", delivery.isConected()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/deliveries/getConnectionStatus")
    public ResponseEntity<ApiResponse> getDeliveryConnectionStatus() {
        try {
            Delivery delivery = authController.getAuthenticatedDelivery();
            return ResponseEntity.ok(ApiResponse.success("Aceptación de pedidos...", delivery.isConected()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/deliveries/updateLocation")
    public ResponseEntity<ApiResponse> updateDeliveryLocation(@RequestBody DeliveryUpdateLocationRequest dulr) {
        try {
            Delivery delivery = authController.getAuthenticatedDelivery();
            delivery.setPosition(dulr.getPosition());
            return ResponseEntity.ok(ApiResponse.success("Ubicación actualizada!", delivery.getPosition()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/customers/getByPhone")
    public ResponseEntity<ApiResponse> getCustomerByPhone(@RequestParam("phone") String phone) {
        Customer customer = userService.getCustomerService().findByPhoneNumber(phone);
        if (customer == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("No se encontro este cliente."));

        }
        return ResponseEntity.ok(ApiResponse.success("customer found!", customer));
    }

    @PostMapping("/customers/createOrUpdate")
    public ResponseEntity<ApiResponse> createOrUpdateCustomer(@RequestBody Customer customer) {
        // Validar que phoneNumber no sea nulo o vacío
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El número de teléfono es obligatorio"));
        }

        // Limpiar phoneNumber (remover caracteres no numéricos)
        String cleanedPhoneNumber = customer.getPhoneNumber().replaceAll("[^0-9]", "");

        // Validar que el phoneNumber tenga exactamente 10 dígitos
        if (cleanedPhoneNumber.length() != 10) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El número de teléfono debe tener 10 dígitos"));
        }

        // Validar que name y address no sean nulos o vacíos
        if (customer.getName() == null || customer.getName().trim().isEmpty()
                || customer.getAddress() == null || customer.getAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El nombre y la dirección son obligatorios"));
        }

        // Establecer el número de teléfono limpio
        customer.setPhoneNumber(cleanedPhoneNumber);

        // Llamar al método merge del servicio
        Customer savedCustomer = userService.getCustomerService().merge(customer);
        // Retornar la respuesta con el cliente en formato JSON
        return ResponseEntity.ok(ApiResponse.success("customer saved!", savedCustomer));
    }

    @PostMapping("/profiles/update")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserProfileDTO profile) {

        UserProfile getProfile = getProfile(profile.getId(), profile.getType());
        if (getProfile != null) {
            getProfile.setName(profile.getName());
            getProfile.setAddress(profile.getAddress());
            getProfile.setPhone(profile.getPhone());
            getProfile.setStatus(profile.getStatus());

        }

        getProfile = saveProfile(getProfile);

        // Retornar la respuesta con el cliente en formato JSON
        return ResponseEntity.ok(ApiResponse.success("customer saved!", getProfile));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "active", required = false) Boolean active) {

        try {
            authController.getAuthenticatedAdmin();

            List<User> users = userService.getUserRepository().findByUsernameAndActiveOptional(username, active);
            return ResponseEntity.ok(ApiResponse.success("Usuarios encontrados", users));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    /**
     * *
     * Elimina un usuario y su cuenta ligada, excepto balance account
     *
     * @param username
     * @return
     */
    @PostMapping("/users/delete")
    public ResponseEntity<ApiResponse> updateUser(@RequestParam String username) {
        try {
            authController.getAuthenticatedAdmin();
            User user = userService.findByUserName(username);

            if (user != null) {
                userService.getUserRepository().delete(user);
                removeRelatedAccount(user);
            }

            // Retornar la respuesta con el cliente en formato JSON
            return ResponseEntity.ok(ApiResponse.success("user removed!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    private boolean removeRelatedAccount(User user) {
        switch (user.getRole()) {
            case Role.ADMIN:
                Admin admin = userService.getAdminService().getById(user.getAccountId());
                if (admin != null) {
                    userService.getAdminService().getAdminRepository().delete(admin);
                    return true;
                }
                break;

            case Role.BUSINESS:
                Business business = userService.getBusinessService().getById(user.getAccountId());
                if (business != null) {
                    userService.getBusinessService().getBusinessRepository().delete(business);
                    return true;
                }
                break;

            case Role.DELIVERY:
                Delivery delivery = userService.getDeliveryService().getById(user.getAccountId());
                if (delivery != null) {
                    userService.getDeliveryService().getDeliveryRepository().delete(delivery);
                    return true;
                }
                break;

            case Role.CUSTOMER:
                Customer customer = userService.getCustomerService().getById(user.getAccountId());
                if (customer != null) {
                    userService.getCustomerService().getCustomerRepository().delete(customer);
                    return true;
                }
                break;
        }

        return false;
    }

    @PostMapping("/users/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserDTO user) {
        try {
            authController.getAuthenticatedAdmin();
            UserDTO saveUser = saveUser(user);

            // Retornar la respuesta con el cliente en formato JSON
            return ResponseEntity.ok(ApiResponse.success("customer saved!", saveUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(e.getMessage()));

        }

    }

    @GetMapping("/profiles/get")
    public ResponseEntity<ApiResponse> getProfilesByTypeAndStatus(
            @RequestParam("type") Role type,
            @RequestParam("status") AccountStatus status) {

        List<UserProfile> profiles;

        switch (type) {
            case ADMIN:
                profiles = userService.getAdminService()
                        .getAdminRepository()
                        .findByAccountStatus(status)
                        .stream()
                        .map(p -> (UserProfile) p)
                        .collect(Collectors.toList());
                break;

            case BUSINESS:
                profiles = userService.getBusinessService()
                        .getBusinessRepository()
                        .findByAccountStatus(status)
                        .stream()
                        .map(p -> (UserProfile) p)
                        .collect(Collectors.toList());
                break;

            case CUSTOMER:
                profiles = userService.getCustomerService()
                        .getCustomerRepository()
                        .findByAccountStatus(status)
                        .stream()
                        .map(p -> (UserProfile) p)
                        .collect(Collectors.toList());
                break;

            case DELIVERY:
                profiles = userService.getDeliveryService()
                        .getDeliveryRepository()
                        .findByAccountStatus(status)
                        .stream()
                        .map(p -> (UserProfile) p)
                        .collect(Collectors.toList());
                break;

            default:
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("Rol no soportado"));
        }

        return ResponseEntity.ok(ApiResponse.success("Perfiles obtenidos", profiles));
    }

    /**
     * *
     * solo guarda el estado actualizado de user
     *
     * @param user
     * @return
     */
    private UserDTO saveUser(UserDTO user) {
        if (user == null) {
            return null;
        }

        User findById = userService.findById(user.getId());
        if (findById != null) {
            findById.setActive(user.isActive());
            return new UserDTO(userService.save(findById));

        }

        return null;

    }

    private UserProfile saveProfile(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        UserProfile existingProfile;

        // Buscar el perfil existente según el tipo de usuario
        switch (userProfile.getType()) {
            case BUSINESS:
                existingProfile = userService.getBusinessService().getBusinessRepository().findById(userProfile.getId())
                        .orElseThrow(() -> new RuntimeException("Business no encontrado"));
                break;
            case DELIVERY:
                existingProfile = userService.getDeliveryService().getDeliveryRepository().findById(userProfile.getId())
                        .orElseThrow(() -> new RuntimeException("Delivery no encontrado"));
                break;
            case ADMIN:
                existingProfile = userService.getAdminService().getAdminRepository().findById(userProfile.getId())
                        .orElseThrow(() -> new RuntimeException("Admin no encontrado"));
                break;
            case CUSTOMER:
                existingProfile = userService.getCustomerService().getCustomerRepository().findById(userProfile.getId())
                        .orElseThrow(() -> new RuntimeException("Customer no encontrado"));
                break;
            default:
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + userProfile.getType());
        }

        // Actualizar solo los campos de UserProfile
        updateUserProfileFields(userProfile, existingProfile);

        // Guardar el perfil actualizado
        switch (existingProfile.getType()) {
            case BUSINESS:
                return userService.getBusinessService().save((Business) existingProfile);
            case DELIVERY:
                return userService.getDeliveryService().save((Delivery) existingProfile);
            case ADMIN:
                return userService.getAdminService().save((Admin) existingProfile);
            case CUSTOMER:
                return userService.getCustomerService().save((Customer) existingProfile);
            default:
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + existingProfile.getType());
        }
    }

// Método para actualizar los campos de UserProfile
    private void updateUserProfileFields(UserProfile source, UserProfile target) {
        if (source.getName() != null) {
            target.setName(source.getName());
        }
        if (source.getAddress() != null) {
            target.setAddress(source.getAddress());
        }
        if (source.getPhone() != null) {
            target.setPhone(source.getPhone());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getBalanceAccount() != null) {
            target.setBalanceAccount(source.getBalanceAccount());
        }
    }

    private UserProfile getProfile(String id, Role role) {
        UserProfile profile = null;
        switch (role) {

            case Role.BUSINESS:
                profile = userService.getBusinessService().getById(id);
                break;

            case Role.DELIVERY:
                profile = userService.getDeliveryService().getById(id);
                break;

            case Role.ADMIN:
                profile = userService.getAdminService().getById(id);
                break;

            case Role.CUSTOMER:
                profile = userService.getCustomerService().getCustomerRepository().getById(id);
                break;

        }

        return profile;

    }

}
