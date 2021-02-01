package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersWorkoutPlanRepository extends JpaRepository<UsersWorkoutPlan, Long> {

    @Query("SELECT p FROM UsersWorkoutPlan p WHERE p.user.id = ?1")
    List<UsersWorkoutPlan> findAllByUserId(Long userId);

    @Query("SELECT p FROM UsersWorkoutPlan p WHERE p.user.id = ?1 AND p.id = ?2")
    Optional<UsersWorkoutPlan> findUsersWorkoutPlanByPlanId(Long userId, Long planId);

}
