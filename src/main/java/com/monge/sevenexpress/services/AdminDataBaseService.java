/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.services;

import com.monge.sevenexpress.dto.ApiResponse;
import com.monge.sevenexpress.entities.Business;
import com.monge.sevenexpress.entities.Customer;
import com.monge.sevenexpress.entities.Delivery;
import com.monge.sevenexpress.entities.User;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Data
public class AdminDataBaseService {

    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private CustomerService customerService;

    public <T> ResponseEntity<ApiResponse> updateEntity(T entity, String entityName) {
        switch (entityName) {
            case "User":
                return updateUser((User) entity);
            case "Delivery":
                return updateDelivery((Delivery) entity);
            case "Business":
                return updateBusiness((Business) entity);
            case "Customer":
                return updateCustomer((Customer) entity);
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Unknown entity"));
        }
    }

    private ResponseEntity<ApiResponse> updateUser(User user) {
        userService.save(user);
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
        List<User> users = userService.getUserRepository().findAll();
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
}
