/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.config;

/**
 *
 * @author DeliveryExpress
 */
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //-> file:/root/sevenexpress/files/
    @Value("${file.upload-dir}")
    private String filesPath;

    @Value("${app.static.paths.home}")
    private String homePath;

    @Value("${app.static.paths.business}")
    private String businessPath;

    @Value("${app.static.paths.deliveries}")
    private String deliveriesPath;

    @Value("${app.static.paths.admins}")
    private String adminsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configuración para recursos de la aplicación
        registry.addResourceHandler("/home/**")
                .addResourceLocations(normalizePath(homePath));

        registry.addResourceHandler("/businessApp/**")
                .addResourceLocations(normalizePath(businessPath));

        registry.addResourceHandler("/deliveries/**")
                .addResourceLocations(normalizePath(deliveriesPath));

        registry.addResourceHandler("/adminsApp/**")
                .addResourceLocations(normalizePath(adminsPath));

        // Configuración para archivos subidos por usuarios
        registry.addResourceHandler("/api/v1/files/**")
                .addResourceLocations(normalizePath(filesPath))
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    private String normalizePath(String path) {
        if (path.startsWith("file:") || path.startsWith("classpath:")) {
            return path.endsWith("/") ? path : path + "/";
        }
        if (!path.endsWith("/")) {
            path += "/";
        }
        return "file:" + path; // agrega "file:" si falta
    }

}
