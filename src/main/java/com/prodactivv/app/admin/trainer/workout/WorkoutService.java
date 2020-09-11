package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.Workout;
import com.prodactivv.app.admin.trainer.models.Workout.WorkoutDTO;
import com.prodactivv.app.admin.trainer.models.ActivityWeek;
import com.prodactivv.app.admin.trainer.models.repositories.WorkoutRepository;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import org.springframework.stereotype.Service;

import static com.prodactivv.app.admin.trainer.models.DetailedExercise.*;
import static com.prodactivv.app.admin.trainer.models.ActivityWeek.*;

@Service
public class WorkoutService {

    private final ExerciseService exerciseService;

    private final WorkoutRepository repository;

    public WorkoutService(ExerciseService exerciseService, WorkoutRepository repository) {
        this.exerciseService = exerciseService;
        this.repository = repository;
    }

    public Workout createWorkout(WorkoutDTO workoutDTO) throws ExerciseNotFoundException {
        Workout workout = new Workout();

        workout.setName(workoutDTO.getName());

        for (DetailedExerciseDTO exercise : workoutDTO.getExercises()) {
            workout.addExercise(exerciseService.provideDetails(exercise));
        }

        return repository.save(workout);
    }
}
