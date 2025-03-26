/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.controllers;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.dto.AuthRequest;
import com.monge.sevenexpress.dto.BusinessRegisterRequest;
import com.monge.sevenexpress.dto.DeliveryRegisterRequest;
import com.monge.sevenexpress.entities.Admin;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import com.monge.sevenexpress.services.AdminService;
import com.monge.sevenexpress.services.BusinessService;
import com.monge.sevenexpress.services.DeliveryService;
import com.monge.sevenexpress.services.JwtService;
import com.monge.sevenexpress.services.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author DeliveryExpress
 */
@RestController
@CrossOrigin("*")
public class PublicController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private AdminService adminService;

    public PublicController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }

    @PostMapping("/business/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        // Verificar las credenciales del usuario
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

            ApiResponse authResponse =  ApiResponse.success("Loggin sucess!", token);
            //authResponse.setData(businessService.getByUserName(loginRequest.getUsername()));

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            // Si las credenciales no son correctas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/business/register")
    public ResponseEntity<?> registerBusiness(@RequestBody BusinessRegisterRequest brr) {

        // Verificar si ya existe un usuario con el mismo nombre
        if (userService.userExistByUserName(brr.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario ya está registrado."));
        }

        // Crear el objeto de Usuario y Business
        User user = new User();
        user.setUserName(brr.getUserName());
        user.setPassword(passwordEncoder.encode(brr.getPassword()));

        Business business = new Business();
        business.setBusinessName(brr.getBusinessName());
        business.setAddress(brr.getAddress());
        business.setPhoneNumber(brr.getPhoneNumber());
        business.setPosition(brr.getPosition());

        //creamos el balanceAccount
        // Guardar en la base de datos
        businessService.save(business);
        user.setAccountId(business.getId());
        user.setRole(User.Role.BUSINESS);
        userService.save(user);

        /*creamos el balance account*/
        businessService.getBalanceAccount(business);
        /*creamos el business account*/
        businessService.getBusinessContract(business);
       

        // Responder con un mensaje de éxito
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("El negocio ha sido registrado exitosamente.", business));
    }

    @PostMapping("/deliveries/login")
    public ResponseEntity<?> loginDelivery(@RequestBody AuthRequest loginRequest) {
        // Verificar las credenciales del usuario
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
            String token = jwtService.generateToken(authentication.getName(), User.Role.DELIVERY);
            System.out.println("Authorized!! token is " + token);

            ApiResponse authResponse = ApiResponse.success("loggin sucess!", token);
           // authResponse.setData(deliveryService.getByUserName(loginRequest.getUsername()));

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            // Si las credenciales no son correctas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/deliveries/register")
    public ResponseEntity<?> registerDeliveries(@RequestBody DeliveryRegisterRequest drr) {

        // Verificar si ya existe un usuario con el mismo nombre
        if (userService.userExistByUserName(drr.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario ya está registrado."));
        }

        // Crear el objeto de Usuario y Business
        User user = new User();
        user.setUserName(drr.getUserName());
        user.setPassword(passwordEncoder.encode(drr.getPassword()));

        Delivery delivery = new Delivery();
        delivery.setName(drr.getName());
        delivery.setAddress(drr.getAddress());
        delivery.setPhoneNumber(drr.getPhoneNumber());

        //creamos el balanceAccount
        // Guardar en la base de datos
        deliveryService.save(delivery);
        user.setAccountId(delivery.getId());
        user.setRole(User.Role.DELIVERY);
        userService.save(user);

        /*creamos el balance account*/
        deliveryService.getBalanceAccount(delivery);

        // Responder con un mensaje de éxito
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("El repartidor ha sido registrado exitosamente.", delivery));
    }

    /*admins public api*/
    @PostMapping("/admins/login")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest loginRequest) {
        // Verificar las credenciales del usuario
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
            String token = jwtService.generateToken(authentication.getName(), User.Role.ADMIN);
            System.out.println("Authorized!! token is " + token);

            ApiResponse authResponse = ApiResponse.success("login sucess!", token);
           // authResponse.setData(adminService.getByUserName(loginRequest.getUsername()));

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            // Si las credenciales no son correctas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
        }
    }

    /**
     * *
     *
     * @param drr drr es compatible para egistrar un admin
     * @return
     */
    @PostMapping("/admins/register")
    public ResponseEntity<?> registerAdmin(@RequestBody DeliveryRegisterRequest drr) {

        // Verificar si ya existe un usuario con el mismo nombre
        if (userService.userExistByUserName(drr.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario ya está registrado."));
        }

        // Crear el objeto de Usuario y Business
        User user = new User();
        user.setUserName(drr.getUserName());
        user.setPassword(passwordEncoder.encode(drr.getPassword()));

        Admin admin = new Admin();
        admin.setName(drr.getName());
        admin.setAddress(drr.getAddress());
        admin.setPhoneNumber(drr.getPhoneNumber());

        //creamos el balanceAccount
        // Guardar en la base de datos
        adminService.save(admin);
        user.setAccountId(admin.getId());
        user.setRole(User.Role.ADMIN);
        userService.save(user);

        /*admins no tienen balance account*/
        // Responder con un mensaje de éxito
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("El admin ha sido registrado exitosamente.", admin));
    }

    @PostMapping({"/deliveries/validatetoken", "/business/validatetoken", "/admins/validatetoken"})

    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        // Verificar si el token está presente en el encabezado Authorization
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Token is missing or invalid"));
        }

        // Extraer el token del encabezado
        String token = authorizationHeader.substring(7); // El prefijo "Bearer " tiene 7 caracteres

        try {
            // Verificar la validez del token
            if (jwtService.validateToken(token)) {
                // Si el token es válido, obtener el nombre del usuario desde el token
                String username = jwtService.extractUsername(token);
                // Si deseas devolver más información sobre el usuario, puedes hacerlo aquí
                return ResponseEntity.ok(ApiResponse.success("Token is valid for user: " + username, true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid or expired token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Error validating token"));
        }
    }

}
