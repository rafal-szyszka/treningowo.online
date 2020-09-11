package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay;
import com.prodactivv.app.admin.trainer.models.ActivityWeek;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityDayRepository;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.models.repositories.ActivityWeekRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.prodactivv.app.admin.trainer.models.ActivityDay.*;
import static com.prodactivv.app.admin.trainer.models.ActivityWeek.*;
import static com.prodactivv.app.admin.trainer.models.DetailedExercise.*;
import static com.prodactivv.app.admin.trainer.models.Workout.*;

@Service
public class ActivityService {

    public static final String ACTIVITY_WEEK_NOT_FOUND_MSG = "Activity week %s not found";
    public static final String ACTIVITY_DAY_NOT_FOUND_MSG = "Activity day %s not found";

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;

    private final ActivityDayRepository repository;
    private final ActivityWeekRepository activityWeekRepository;

    public ActivityService(WorkoutService workoutService, ExerciseService exerciseService, ActivityDayRepository repository, ActivityWeekRepository activityWeekRepository) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.repository = repository;
        this.activityWeekRepository = activityWeekRepository;
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

    public Optional<ActivityDay> createActivityDay(ActivityDayManagerDTO activityDayDTO) throws ExerciseNotFoundException {
        if (activityDayDTO != null) {
            ActivityDay activityDay = new ActivityDay();

            activityDay.setName(activityDayDTO.getName());
            activityDay.setTips(activityDayDTO.getTips());

            if (activityDayDTO.getExercises() != null) {
                for (DetailedExerciseManagerDTO exerciseSimpleDTO : activityDayDTO.getExercises()) {
                    activityDay.addDetailedExercise(
                            exerciseService.provideDetails(exerciseSimpleDTO)
                    );
                }
            }

            return Optional.of(repository.save(activityDay));
        }
        return Optional.empty();
    }

    public ActivityWeek createActivityWeek(ActivityWeekDTO activityWeekDTO) throws ExerciseNotFoundException {
        ActivityWeek activityWeek = new ActivityWeek();
        activityWeek.setName(activityWeekDTO.getName());

        for (ActivityDayDTO activityDayDTO : activityWeekDTO.getActivityDays()) {
            Optional<ActivityDay> activityDay = createActivityDay(activityDayDTO);
            activityDay.ifPresent(activityWeek::addDay);
        }

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityWeek getActivityWeekById(Long id) throws NotFoundException {
        return activityWeekRepository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));
    }

    public ActivityWeek addActivityDayToActivityWeek(Long id, ActivityDayDTO activityDayDTO) throws NotFoundException, ExerciseNotFoundException {
        ActivityWeek activityWeek = getActivityWeekById(id);

        createActivityDay(activityDayDTO).ifPresent(activityWeek::addDay);

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityWeek addActivityDayToActivityWeek(Long id, ActivityDayManagerDTO activityDayDTO) throws NotFoundException, ExerciseNotFoundException {
        ActivityWeek activityWeek = getActivityWeekById(id);

        createActivityDay(activityDayDTO).ifPresent(activityWeek::addDay);

        return activityWeekRepository.save(activityWeek);
    }

    public ActivityDay addExerciseToActivityDay(Long id, DetailedExerciseDTO exerciseDTO) throws NotFoundException, ExerciseNotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_DAY_NOT_FOUND_MSG, id)));

        activityDay.addDetailedExercise(exerciseService.provideDetails(exerciseDTO));

        return repository.save(activityDay);
    }

    public ActivityWeekManagerDTO removeActivityWeekFromUserPlan(Long id) throws NotFoundException {
        ActivityWeek activityWeek = activityWeekRepository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));

        activityWeek.getPlans().forEach(workoutPlan -> workoutPlan.removeActivityWeek(activityWeek));
        activityWeek.delete();
        activityWeekRepository.delete(activityWeek);

        return ActivityWeekManagerDTO.of(activityWeek).orElseThrow(new NotFoundException(String.format(ACTIVITY_WEEK_NOT_FOUND_MSG, id)));
    }

    public ActivityDayManagerDTO removeActivityDayFromActivityWeek(Long id) throws NotFoundException {
        ActivityDay activityDay = repository.findById(id).orElseThrow(new NotFoundException(String.format(ACTIVITY_DAY_NOT_FOUND_MSG, id)));

        activityDay.getActivityWeeks().forEach(activityWeek -> activityWeek.removeDay(activityDay));
        activityDay.delete();
        repository.delete(activityDay);

        return ActivityDayManagerDTO.of(activityDay);
    }

    public DetailedExerciseManagerDTO removeExerciseFromActivityDay(Long id) throws ExerciseNotFoundException {
        return DetailedExerciseManagerDTO.of(exerciseService.deleteDetailedExercise(id));
    }
}
