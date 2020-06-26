package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
