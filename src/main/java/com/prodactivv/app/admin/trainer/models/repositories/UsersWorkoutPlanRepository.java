package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersWorkoutPlanRepository extends JpaRepository<UsersWorkoutPlan, Long> {
}
