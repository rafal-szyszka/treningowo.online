package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayManagerDTO;
import com.prodactivv.app.admin.trainer.models.ActivityDaySuperExercise.ActivityDaySuperExerciseManagerDto;
import com.prodactivv.app.admin.trainer.models.ActivityWeek;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekManagerDTO;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.admin.trainer.models.repositories.WorkoutPlanRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import static com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseManagerDTO;

@Service
public class WorkoutPlanService {

    public static final String WORKOUT_PLAN_NOT_FOUND_MSG = "Workout plan %s not found";

    private final WorkoutPlanRepository repository;
    private final ActivityService activityService;

    public WorkoutPlanService(WorkoutPlanRepository repository, ActivityService activityService) {
        this.repository = repository;
        this.activityService = activityService;
    }

    public WorkoutPlan createWorkoutPlan(WorkoutPlanDTO workoutPlanDTO) throws ExerciseNotFoundException {
        WorkoutPlan plan = new WorkoutPlan();

        plan.setName(workoutPlanDTO.getName());
        for (ActivityWeekDTO activityWeekDTO: workoutPlanDTO.getActivityWeeks()) {
            plan.addWorkoutWeek(activityService.createActivityWeek(activityWeekDTO));
        }

        return repository.save(plan);
    }

    public ActivityWeekManagerDTO addEmptyActivityWeekToUserPlan(Long id, ActivityWeekDTO activityWeekDTO) throws NotFoundException, ExerciseNotFoundException {
        WorkoutPlan workoutPlan = repository.findById(id).orElseThrow(new NotFoundException(String.format(WORKOUT_PLAN_NOT_FOUND_MSG, id)));

        ActivityWeek activityWeek = activityService.createActivityWeek(activityWeekDTO);
        workoutPlan.addWorkoutWeek(activityWeek);

        repository.save(workoutPlan);

        return ActivityWeekManagerDTO.of(activityWeek).orElseThrow(NotFoundException::new);
    }

    public ActivityWeekManagerDTO addEmptyActivityDayToActivityWeek(Long id, ActivityDayManagerDTO activityDayDTO) throws Exception {
        return ActivityWeekManagerDTO.of(activityService.addActivityDayToActivityWeek(id, activityDayDTO)).orElseThrow(Exception::new);
    }

    public ActivityDayManagerDTO addExerciseToActivityDay(Long id, DetailedExerciseDTO exerciseDTO, Long order) throws NotFoundException, ExerciseNotFoundException {
        return ActivityDayManagerDTO.of(activityService.addExerciseToActivityDay(id, exerciseDTO, order));
    }

    public ActivityDayManagerDTO addExerciseToActivityDay(Long id, Long detailedExerciseId, Long order) throws NotFoundException, ExerciseNotFoundException {
        return ActivityDayManagerDTO.of(activityService.addExerciseToActivityDay(id, detailedExerciseId, order));
    }

    public ActivityWeekManagerDTO removeActivityWeekFromUserPlan(Long id) throws NotFoundException {
        return activityService.removeActivityWeekFromUserPlan(id);
    }

    public ActivityDayManagerDTO removeActivityDayFromActivityWeek(Long id) throws NotFoundException {
        return activityService.removeActivityDayFromActivityWeek(id);
    }

    public DetailedExerciseManagerDTO removeExerciseFromActivityDay(Long id) throws ExerciseNotFoundException {
        return activityService.removeExerciseFromActivityDay(id);
    }

    public ActivityDayManagerDTO setActivityDayTips(Long id, String tips) throws NotFoundException {
        return activityService.setActivityDayTips(id, tips);
    }

    public ActivityDaySuperExerciseManagerDto moveExerciseByStep(Long id, Long step) throws NotFoundException {
        return activityService.moveExerciseByStep(id, step);
    }
}
