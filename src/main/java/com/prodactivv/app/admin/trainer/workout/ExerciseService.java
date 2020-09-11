package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.*;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.models.repositories.DetailedExerciseRepository;
import com.prodactivv.app.admin.trainer.models.repositories.ExerciseRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository repository;
    private final DetailedExerciseRepository detailedExerciseRepository;

    public ExerciseService(ExerciseRepository repository, DetailedExerciseRepository detailedExerciseRepository) {
        this.repository = repository;
        this.detailedExerciseRepository = detailedExerciseRepository;
    }

    public Exercise createExercise(Exercise exercise) {
        return repository.save(exercise);
    }

    public Exercise updateExercise(Long id, Exercise exercise) throws ExerciseNotFoundException {
        Exercise prevExercise = repository.findById(id).orElseThrow(new ExerciseNotFoundException(id));
        return repository.save(prevExercise.update(exercise));
    }

    public Exercise deleteExercise(Long id) throws ExerciseNotFoundException {
        Exercise exercise = repository.findById(id).orElseThrow(new ExerciseNotFoundException(id));
        repository.delete(exercise);
        exercise.setId(-1L);
        return exercise;
    }

    public Exercise getExercise(Long id) throws ExerciseNotFoundException {
        return repository.findById(id).orElseThrow(new ExerciseNotFoundException(id));
    }

    public List<Exercise> getExercises() {
        return repository.findAll();
    }

    public DetailedExercise provideDetails(DetailedExerciseDTO detailedExerciseDTO) throws ExerciseNotFoundException {
        Exercise exercise = repository.findById(detailedExerciseDTO.getExerciseId())
                .orElseThrow(new ExerciseNotFoundException(detailedExerciseDTO.getExerciseId()));
        return detailedExerciseRepository.save(new DetailedExercise(detailedExerciseDTO, exercise));
    }

    public DetailedExercise getDetailedExercise(Long id) throws ExerciseNotFoundException {
        return detailedExerciseRepository.findById(id).orElseThrow(new ExerciseNotFoundException(id));
    }

    public DetailedExercise deleteDetailedExercise(Long id) throws ExerciseNotFoundException {
        DetailedExercise detailedExercise = detailedExerciseRepository.findById(id).orElseThrow(new ExerciseNotFoundException(id));
        detailedExerciseRepository.delete(detailedExercise);
        detailedExercise.setId(-1L);
        return detailedExercise;
    }
}
