/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByUserName(String userName);

    Optional<User> findByAccountId(String accountId);

    boolean existsByUserName(String userName);

    @Query("SELECT u FROM User u WHERE "
            + "(:username IS NULL OR u.userName LIKE %:username%) AND "
            + "(:active IS NULL OR u.active = :active)")
    List<User> findByUsernameAndActiveOptional(@Param("username") String username,
            @Param("active") Boolean active);

}
