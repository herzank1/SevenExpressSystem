/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.config;


/**
 *
 * @author DeliveryExpress
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.static.path.business}")
    private String businessPath;

    @Value("${app.static.path.admins}")
    private String adminsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/businessApp/**")
                .addResourceLocations(businessPath);

        registry.addResourceHandler("/adminsApp/**")
                .addResourceLocations(adminsPath);
    }
}
