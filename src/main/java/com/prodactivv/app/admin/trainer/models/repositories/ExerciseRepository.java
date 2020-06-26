package com.prodactivv.app.admin.trainer.models.repositories;

import com.prodactivv.app.admin.trainer.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
