package com.prodactivv.app.user.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDietRepository extends JpaRepository<UserDiet, Long> {

    @Query("SELECT ud FROM UserDiet ud WHERE ud.user.id = ?1")
    List<UserDiet> findAllUserDiets(Long id);
}