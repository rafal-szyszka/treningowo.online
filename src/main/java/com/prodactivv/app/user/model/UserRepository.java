package com.prodactivv.app.user.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 'user' AND COALESCE(u.isActive, true) = true")
    List<User> findAllUsers();

    @Query("SELECT u FROM User u WHERE u.role = 'DIETITIAN' AND COALESCE(u.isActive, true) = true")
    List<User> findAllDietitians();

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND COALESCE(u.isActive, true) = true")
    List<User> findAllAdmins();
}
