package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/admin/workout")
public class WorkoutPrepController {

    private final WorkoutPlanService workoutPlanService;
    private final UsersWorkoutPlanService usersWorkoutPlanService;

    public WorkoutPrepController(WorkoutPlanService workoutPlanService, UsersWorkoutPlanService usersWorkoutPlanService) {
        this.workoutPlanService = workoutPlanService;
        this.usersWorkoutPlanService = usersWorkoutPlanService;
    }

    @PostMapping(value = "/create/complete/forUser/{id}")
    public ResponseEntity<UsersWorkoutPlan> createCompleteWorkoutPlan(
            @PathVariable Long id,
            @RequestBody WorkoutPlanDTO plan) {
        try {
            return ResponseEntity.ok(
                    usersWorkoutPlanService.createUsersWorkoutPlan(
                            id, workoutPlanService.createWorkoutPlan(plan)
                    )
            );
        } catch (UserNotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/create/partial/forUser/{id}")
    public ResponseEntity<UsersWorkoutPlan> createPartialWorkoutPlan(
            @PathVariable Long id,
            @RequestBody WorkoutPlanDTO plan) {
        try {
            return ResponseEntity.ok(
                    usersWorkoutPlanService.createUsersWorkoutPlan(id, workoutPlanService.createWorkoutPlan(plan))
            );
        } catch (UserNotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
