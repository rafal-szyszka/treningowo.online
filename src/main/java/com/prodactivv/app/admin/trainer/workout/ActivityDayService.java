package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay;
import com.prodactivv.app.admin.trainer.models.Workout;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityDayRepository;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.prodactivv.app.admin.trainer.models.ActivityDay.*;
import static com.prodactivv.app.admin.trainer.models.Workout.*;

@Service
public class ActivityDayService {

    private final WorkoutService workoutService;

    private final ActivityDayRepository repository;

    public ActivityDayService(WorkoutService workoutService, ActivityDayRepository repository) {
        this.workoutService = workoutService;
        this.repository = repository;
    }

    public Optional<ActivityDay> createActivityDay(ActivityDayDTO activityDayDTO) throws ExerciseNotFoundException {
        if (activityDayDTO != null) {
            ActivityDay activityDay = new ActivityDay();

            activityDay.setName(activityDayDTO.getName());
            activityDay.setTips(activityDayDTO.getTips());

            for (WorkoutDTO workoutDTO : activityDayDTO.getWorkouts()) {
                activityDay.addWorkout(workoutService.createWorkout(workoutDTO));
            }

            return Optional.of(repository.save(activityDay));
        }
        return Optional.empty();
    }

}
