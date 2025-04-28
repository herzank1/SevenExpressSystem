/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.apiv1;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.AuthRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.entities.User.Role;
import com.monge.sevenexpress.subservices.JwtService;
import com.monge.sevenexpress.services.UserService;
import com.monge.sevenexpress.services.UtilitiesService;
import com.monge.sevenexpress.utils.DataValidators;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Diego Villarreal
 * Controlador encargado de manejar las solicitudes de autenticacion, login, register, logout
 * para las diferentes tipos de cuentas DELIVERY, BUSINES, ADMINS, CUSTOMER.
 * Los endopoints no requieren autorizacion solo /logout
 * 
 */
@RestController
@RequestMapping("/api/v1") // Prefijo para todas las rutas
public class AuthController {

    private boolean autoEnableAccounts = false;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilitiesService utilitiesService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest loginRequest) {
        

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Obtener el usuario desde el contexto de seguridad
            String username = loginRequest.getUsername();
            User user = userService.findByUserName(username);

            

            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("account not exist!"));
            }

            // Verificar si la cuenta está activa
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Your account is not activated yet."));
            }

            // Si la autenticación es exitosa, generamos el JWT
            String token = jwtService.generateToken(authentication.getName(), User.Role.BUSINESS);
            System.out.println("Authorized!! token is " + token);

            ApiResponse authResponse = ApiResponse.success("Loggin sucess!", token);
            //authResponse.setData(businessService.getByUserName(loginRequest.getUsername()));

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            // Si las credenciales no son correctas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
        }
    }

    /**
     * *
     * El usuario debera indicar el tipo de rol en el registro
     *
     * @param body
     * @return
     */
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> register(@RequestBody Map<String, Object> body) {
        try {
            /*Obtenemos el role del usuarioa  registrar*/
            User.Role role = User.Role.valueOf((String) body.get("role"));

            /*extraemos el username del body map*/
            String userName = (String) body.get("userName");

            /*validamos el user name*/
            if (!DataValidators.isValidUserName(userName)) {
                return ResponseEntity.ok(ApiResponse.error("El nombre de usuario es invalido"));

            }

            /*extraemos el password*/
            String password = (String) body.get("password");

            /*validamos el password*/
            if (!DataValidators.isValidPassword(password)) {
                return ResponseEntity.ok(ApiResponse.error("El password es invalido"));
            }

            // Verificar si ya existe un usuario con el mismo nombre
            if (userService.userExistByUserName(userName)) {
                return ResponseEntity.ok(ApiResponse.error("El nombre de usuario ya está registrado."));
            }

            // Crear el objeto de Usuario
            User user = new User();
            user.setActive(autoEnableAccounts);
            user.setUserName(userName);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            /*declaramos la entidad generica nulla para almacenar la cuenta a guardar*/
            Object savedEntity = null;

            /*extraemos y validamos phoneNumber para todos los registros*/
            String phoneNumber = (String) body.get("phoneNumber");

            if (!DataValidators.isValidPhoneNumber(phoneNumber)) {
                return ResponseEntity.ok(ApiResponse.error("El numero es invalido, debe contener 10 digitos sin caracteres especiales."));
            }

            switch (role) {
                case BUSINESS -> {
                    Business business = new Business();
                    business.setBusinessName((String) body.get("businessName"));

                    String address = (String) body.get("address");

                    if (!DataValidators.isValidAddress(address)) {
                        return ResponseEntity.ok(ApiResponse.error("El formato de la direccion es invalido, utiliza una direccion de google maps."));
                    }

                    business.setAddress(address);
                    business.setPhoneNumber(phoneNumber);

                    String position = (String) body.get("position");

                    if (!DataValidators.isValidCoordinates(position)) {
                        /*buscamos coordenadas segun google maps api*/
                        position = utilitiesService.getGoogleMapsService().addressToPosition(address);
                    }

                    business.setPosition(position);

                    /*auto activacion de la cuenta*/
                    if (autoEnableAccounts) {
                        business.setAccountStatus(Business.AccountStatus.ACTIVADO);
                    }
                    business = userService.getBusinessService().save(business);
                    user.setAccountId(business.getId());
                    savedEntity = business;
                }
                case DELIVERY -> {
                    Delivery delivery = new Delivery();
                    delivery.setName((String) body.get("name"));
                    delivery.setAddress((String) body.get("address"));
                    delivery.setPhoneNumber(phoneNumber);
                    if (autoEnableAccounts) {
                        delivery.setAccountStatus(Business.AccountStatus.ACTIVADO);
                    }
                    delivery = userService.getDeliveryService().save(delivery);
                    user.setAccountId(delivery.getId());
                    savedEntity = delivery;
                }
                case ADMIN -> {
                    Admin admin = new Admin();
                    admin.setName((String) body.get("name"));
                    admin.setAddress((String) body.get("address"));
                    admin.setPhoneNumber(phoneNumber);
                    admin = userService.getAdminService().save(admin);
                    user.setAccountId(admin.getId());
                    savedEntity = admin;
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Unsupported role"));
                }
            }

            /*guardamos el objeto usuario despues de guardar el objeto Account*/
            userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Usuario registrado exitosamente", savedEntity));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));

        }

    }

    /**
     * *
     * Endpoint para validar el token de sesion el cliente debera manejar la logica 
     * necesaria para su loggin
     *
     * @param authorizationHeader
     * @return
     */
    @PostMapping("/auth/validateToken")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Token is missing or invalid"));
        }

        String token = authorizationHeader.substring(7);

        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                return ResponseEntity.ok(ApiResponse.success("Token is valid for user: " + username, true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Error validating token"));
        }
    }

    /**
     * *
     * Endpoint para revocar el token de sesion el cliente debera hacer logout
     *
     * @param token
     * @return
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Quitamos "Bearer " del token
        }

        jwtService.revokeToken(token);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada correctamente", null));

    }

    /**
     * *
     *
     * @return regresa la cuenta del usuario de tipo
     * DELIVERY, BUSINES, ADMINS, CUSTOMER dependeido del SecurityContextHolder
     * 
     */
    @GetMapping("/auth/account")
    public ResponseEntity<ApiResponse> account() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User is not authenticated"));
        }

        String username = authentication.getName();

        // Extraer roles
        Set<String> roles = authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        if (roles.contains(Role.ADMIN.name())) {
            Admin admin = userService.getAdminByUserName(username);
            return ResponseEntity.ok(ApiResponse.success("your admin account", admin));
        } else if (roles.contains(Role.BUSINESS.name())) {
            Business business = userService.getBusinessByUserName(username);
            return ResponseEntity.ok(ApiResponse.success("your business account", business));
        } else if (roles.contains(Role.DELIVERY.name())) {
            Delivery delivery = userService.getDeliveryByUserName(username);
            return ResponseEntity.ok(ApiResponse.success("your delivery account", delivery));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Role not authorized or not recognized"));
        }
    }

    // Método común para manejar la autenticación de un usario
    public User getAuthenticatedUser() throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationServiceException("User is not authenticated");
        }

        // Si usas UserDetails en lugar de User, asegúrate de que el cast sea correcto
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;  // O usa el tipo de usuario adecuado si es diferente
        } else {
            throw new AuthenticationServiceException("User details are not of expected type");
        }
    }

    // Método común para manejar la autenticación y obtener el Delivery
    public Business getAuthenticatedBusiness() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        Business business = userService.getBusinessByUserName(authentication.getName());
        if (business == null) {
            throw new Exception("Business not found");
        }
        return business;
    }

    // Método común para manejar la autenticación y obtener la cuenta de Delivery
    public Delivery getAuthenticatedDelivery() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        Delivery delivery = userService.getDeliveryByUserName(authentication.getName());
        if (delivery == null) {
            throw new Exception("Delivery not found");
        }
        return delivery;
    }

    // Método común para manejar la autenticación y obtener la cuenta de Admin
    public Admin getAuthenticatedAdmin() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        Admin admin = userService.getAdminByUserName(authentication.getName());
        if (admin == null) {
            throw new Exception("Admin not found");
        }
        return admin;
    }

    /***
     * 
     * @return la cuenta autenticada y asociada del usuario
     * @throws Exception 
     */
    public Object getAnyAuthenticated() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        User principal = (User) authentication.getPrincipal();

        Object account = null;

        switch (principal.getRole()) {

            case BUSINESS:
                account = userService.getBusinessByUserName(authentication.getName());
                if (account == null) {
                    throw new Exception("Business not found");
                }
                break;

            case DELIVERY:
                account = userService.getDeliveryByUserName(authentication.getName());
                if (account == null) {
                    throw new Exception("Delivery not found");
                }
                break;

            case ADMIN:
                account = userService.getAdminByUserName(authentication.getName());
                if (account == null) {
                    throw new Exception("Admin not found");
                }
                break;
        }

        return account; // ¡Retornar la cuenta!
    }

}
