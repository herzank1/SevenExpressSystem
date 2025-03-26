package com.monge.sevenexpress.config;

import com.monge.sevenexpress.security.JwtAuthenticationFilter;
import com.monge.sevenexpress.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain businessSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/business/**") // Aplica reglas a todo lo que esté en "/business/**"
                .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a login y register (HTML + API)
                .requestMatchers("/business/**").permitAll()
                // Restringir acceso a `/business/home` y la API `/api/business/**`
 // Permitir acceso público a archivos estáticos en "/static/business/**"
            .requestMatchers("/businessApp/**").permitAll()
                .requestMatchers("/api/business/**").hasAuthority("BUSINESS") // Se requiere el rol BUSINESS

                // Cualquier otra URL en "/business/**" requiere autenticación
                .anyRequest().authenticated()
                )
                // 🔴 Deshabilita CSRF completamente si usas JWT
                .csrf(csrf -> csrf.disable())
                // Política de sesión (JWT no usa sesiones)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agregar el filtro de JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
       @Bean
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/customers/**") // Aplica reglas a todo lo que esté en "/customers/**"
                .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a login y register (HTML + API)
                .requestMatchers("/customers/**").permitAll()
               
                .requestMatchers("/api/customers/**").hasAuthority("CUSTOMER") // Se requiere el rol BUSINESS

                // Cualquier otra URL en "/business/**" requiere autenticación
                .anyRequest().authenticated()
                )
                // 🔴 Deshabilita CSRF completamente si usas JWT
                .csrf(csrf -> csrf.disable())
                // Política de sesión (JWT no usa sesiones)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agregar el filtro de JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
        @Bean
    public SecurityFilterChain deliverySecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/deliveries/**") // Aplica reglas a todo lo que esté en "/business/**"
                .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a login y register (HTML + API)
                .requestMatchers("/deliveries/**").permitAll()
                // Restringir acceso a `/business/home` y la API `/api/business/**`

                .requestMatchers("/api/deliveries/**").hasAuthority("DELIVERY") // Se requiere el rol BUSINESS

                // Cualquier otra URL en "/business/**" requiere autenticación
                .anyRequest().authenticated()
                )
                // 🔴 Deshabilita CSRF completamente si usas JWT
                .csrf(csrf -> csrf.disable())
                // Política de sesión (JWT no usa sesiones)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agregar el filtro de JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
      @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admins/**") // Aplica reglas a todo lo que esté en "/business/**"
                .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a login y register (HTML + API)
                .requestMatchers("/admins/**").permitAll()
                // Restringir acceso a `/business/home` y la API `/api/business/**`
 // Permitir acceso público a archivos estáticos en "/static/business/**"
            .requestMatchers("/adminsApp/**").permitAll()
                .requestMatchers("/api/admins/**").hasAuthority("ADMIN") // Se requiere el rol BUSINESS

                // Cualquier otra URL en "/business/**" requiere autenticación
                .anyRequest().authenticated()
                )
                // 🔴 Deshabilita CSRF completamente si usas JWT
                .csrf(csrf -> csrf.disable())
                // Política de sesión (JWT no usa sesiones)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Agregar el filtro de JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
    
        @PostConstruct
    public void init() {
        // Verifica si el filtro fue correctamente creado e inyectado
        System.out.println("JwtAuthenticationFilter ha sido inyectado.");
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración para servir archivos estáticos
    @Configuration
    public class StaticResourceConfig {

        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry registry) {
                    registry.addResourceHandler("/**")
                            .addResourceLocations("classpath:/static/");
                }
            };
        }
    }
}
