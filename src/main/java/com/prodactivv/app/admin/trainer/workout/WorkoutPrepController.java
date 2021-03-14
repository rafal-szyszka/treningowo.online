package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityDay.ActivityDayManagerDTO;
import com.prodactivv.app.admin.trainer.models.ActivityDaySuperExercise.ActivityDaySuperExerciseManagerDto;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseDTO;
import com.prodactivv.app.admin.trainer.models.DetailedExercise.DetailedExerciseManagerDTO;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan.WorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.exceptions.ExerciseNotFoundException;
import com.prodactivv.app.config.Strings;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import static com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekManagerDTO;

@RestController
@RequestMapping(value = "/admin/workout")
public class WorkoutPrepController {

    private final Strings strings;
    private final WorkoutPlanService workoutPlanService;
    private final UsersWorkoutPlanService usersWorkoutPlanService;

    public WorkoutPrepController(Strings strings, WorkoutPlanService workoutPlanService, UsersWorkoutPlanService usersWorkoutPlanService) {
        this.strings = strings;
        this.workoutPlanService = workoutPlanService;
        this.usersWorkoutPlanService = usersWorkoutPlanService;
    }

    @PostMapping(value = "/create/complete/forUser/{id}")
    public ResponseEntity<UsersWorkoutPlanDTO> createCompleteWorkoutPlan(
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
    public ResponseEntity<UsersWorkoutPlanDTO> createPartialWorkoutPlan(
            @PathVariable Long id,
            @RequestBody(required = false) WorkoutPlanDTO plan) {
        try {
            return ResponseEntity.ok(
                    usersWorkoutPlanService.createUsersWorkoutPlan(
                            id,
                            plan.isNotEmpty() ? workoutPlanService.createWorkoutPlan(plan) :
                                    workoutPlanService.createWorkoutPlan(WorkoutPlanDTO.getEmpty(
                                            strings.getWorkoutPlanDefaultName(),
                                            strings.getWorkoutPlanWeekDefaultName(),
                                            strings.getWorkoutPlanWeekDayDefaultName()
                                    ))
                    )
            );
        } catch (UserNotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/manage/plan/{id}/addNewActivityWeek")
    public ResponseEntity<ActivityWeekManagerDTO> addEmptyActivityWeekToUserPlan(
            @PathVariable Long id, @RequestBody ActivityWeekDTO activityWeekDTO
    ) {
        try {
            return ResponseEntity.ok(
                    workoutPlanService.addEmptyActivityWeekToUserPlan(id, activityWeekDTO)
            );
        } catch (NotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/manage/plan/activityWeek/{id}/addNewActivityDay")
    public ResponseEntity<ActivityWeekManagerDTO> addEmptyActivityDayToActivityWeek(
            @PathVariable Long id, @RequestBody ActivityDayManagerDTO activityDayDTO
    ) {
        try {
            return ResponseEntity.ok(
                    workoutPlanService.addEmptyActivityDayToActivityWeek(id, activityDayDTO)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/manage/plan/activityDay/{id}/addNewExercise/{order}")
    public ResponseEntity<ActivityDayManagerDTO> addExerciseToActivityDay(
            @PathVariable Long id, @RequestBody DetailedExerciseDTO exerciseDTO, @PathVariable Long order
    ) {
        try {
            return ResponseEntity.ok(
                    workoutPlanService.addExerciseToActivityDay(id, exerciseDTO, order)
            );
        } catch (NotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/manage/plan/activityDay/{id}/addNewExerciseById/{order}")
    public ResponseEntity<ActivityDayManagerDTO> addExerciseToActivityDayById(
            @PathVariable Long id, @RequestParam Long deId, @PathVariable(required = false) Long order
    ) {
        try {
            return ResponseEntity.ok(
                    workoutPlanService.addExerciseToActivityDay(id, deId, order)
            );
        } catch (NotFoundException | ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/activityDay/exercise/{id}/moveBy/{step}")
    public ResponseEntity<ActivityDaySuperExerciseManagerDto> moveExerciseByStep(@PathVariable Long id, @PathVariable Long step) {
        try {
            return ResponseEntity.ok(workoutPlanService.moveExerciseByStep(id, step));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/manage/plan/activityWeek/{id}/delete")
    public ResponseEntity<ActivityWeekManagerDTO> removeActivityWeekFromUserPlan(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(workoutPlanService.removeActivityWeekFromUserPlan(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/manage/plan/activityDay/{id}/delete")
    public ResponseEntity<ActivityDayManagerDTO> removeActivityDayFromActivityWeek(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(workoutPlanService.removeActivityDayFromActivityWeek(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/manage/plan/exercise/{id}/delete")
    public ResponseEntity<DetailedExerciseManagerDTO> removeExerciseFromActivityDay(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(workoutPlanService.removeExerciseFromActivityDay(id));
        } catch (ExerciseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/get/user/{userId}/plans")
    public ResponseEntity<List<UsersWorkoutPlanDTO>> getUsersWorkoutPlans(@PathVariable Long userId) {
        return ResponseEntity.ok(usersWorkoutPlanService.getUserWorkoutPlans(userId));
    }

    @GetMapping(value = "/get/user/{userId}/plans/{planId}")
    public ResponseEntity<UsersWorkoutPlanDTO> getUserWorkoutPlan(@PathVariable Long userId, @PathVariable Long planId) {
        try {
            return ResponseEntity.ok(usersWorkoutPlanService.getUserWorkoutPlan(userId, planId));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/activityDay/{id}/setTips")
    public ResponseEntity<ActivityDayManagerDTO> setTips(@PathVariable Long id, @RequestBody String tips) {
        try {
            return ResponseEntity.ok(workoutPlanService.setActivityDayTips(id, tips));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/{id}/activate")
    public ResponseEntity<UsersWorkoutPlanDTO> activatePlan(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usersWorkoutPlanService.activate(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/{id}/deactivate")
    public ResponseEntity<UsersWorkoutPlanDTO> deactivatePlan(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usersWorkoutPlanService.deactivate(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/{id}/rename")
    public ResponseEntity<String> renamePlan(@PathVariable Long id, @RequestParam String name) {
        try {
            return ResponseEntity.ok(usersWorkoutPlanService.rename(id, name));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/manage/plan/{id}/setActiveDate")
    public ResponseEntity<UsersWorkoutPlanDTO> setActiveDate(@PathVariable Long id, @RequestParam String date) {
        try {
            return ResponseEntity.ok(usersWorkoutPlanService.setActiveDate(id, date));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
