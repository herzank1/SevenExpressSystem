/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.monge.sevenexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.monge.sevenexpress.repositories")
@ComponentScan(basePackages = "com.monge")
public class SevenExpress {
    public static void main(String[] args) {
        SpringApplication.run(SevenExpress.class, args);
    }
}