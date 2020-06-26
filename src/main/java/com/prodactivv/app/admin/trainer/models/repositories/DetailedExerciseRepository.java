package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.DetailedExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailedExerciseRepository extends JpaRepository<DetailedExercise, Long> {
}
