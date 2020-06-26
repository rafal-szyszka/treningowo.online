package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.repositories.WorkoutPlanRepository;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WorkoutPlanService {

    private final WorkoutPlanRepository repository;
    private final ActivityDayService activityDayService;

    public WorkoutPlanService(WorkoutPlanRepository repository, ActivityDayService activityDayService) {
        this.repository = repository;
        this.activityDayService = activityDayService;
    }

    public WorkoutPlan createWorkoutPlan(WorkoutPlanDTO workoutPlanDTO) throws ExerciseNotFoundException {
        WorkoutPlan plan = new WorkoutPlan();

        plan.setName(workoutPlanDTO.getName());
        activityDayService.createActivityDay(workoutPlanDTO.getMonday()).ifPresent(plan::setMonday);
        activityDayService.createActivityDay(workoutPlanDTO.getTuesday()).ifPresent(plan::setTuesday);
        activityDayService.createActivityDay(workoutPlanDTO.getWednesday()).ifPresent(plan::setWednesday);
        activityDayService.createActivityDay(workoutPlanDTO.getThursday()).ifPresent(plan::setThursday);
        activityDayService.createActivityDay(workoutPlanDTO.getFriday()).ifPresent(plan::setFriday);
        activityDayService.createActivityDay(workoutPlanDTO.getSaturday()).ifPresent(plan::setSaturday);
        activityDayService.createActivityDay(workoutPlanDTO.getSunday()).ifPresent(plan::setSunday);

        return repository.save(plan);
    }

}
