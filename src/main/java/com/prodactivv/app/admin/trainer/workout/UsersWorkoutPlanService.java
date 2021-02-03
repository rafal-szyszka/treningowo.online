package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan;
import com.prodactivv.app.admin.trainer.models.repositories.UsersWorkoutPlanRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersWorkoutPlanService {

    public static final String MSG_PLAN_FOR_USER_NOT_FOUND = "Plan %s for user %s was not found!";
    private final UsersWorkoutPlanRepository repository;
    private final UserRepository userRepository;

    public UsersWorkoutPlanService(UsersWorkoutPlanRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public UsersWorkoutPlanDTO createUsersWorkoutPlan(Long userId, WorkoutPlan workoutPlan) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(new UserNotFoundException(userId));

        UsersWorkoutPlan plan = new UsersWorkoutPlan();
        plan.setUser(user);
        plan.setCreatedAt(LocalDate.now());
        plan.setIsActive(false);
        if (workoutPlan != null) {
            plan.setWorkoutPlan(workoutPlan);
        }

        return UsersWorkoutPlanDTO.of(repository.save(plan));
    }

    public List<UsersWorkoutPlanDTO> getUserWorkoutPlans(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(UsersWorkoutPlanDTO::of)
                .collect(Collectors.toList());
    }

    public UsersWorkoutPlanDTO getUserWorkoutPlan(Long userId, Long planId) throws NotFoundException {
        return UsersWorkoutPlanDTO.of(
                repository.findUsersWorkoutPlanByPlanId(userId, planId)
                        .orElseThrow(new NotFoundException(
                                String.format(MSG_PLAN_FOR_USER_NOT_FOUND, planId, userId)
                        ))
        );
    }

//    public UsersWorkoutPlanDTO copy(Long id) throws NotFoundException {
//        UsersWorkoutPlan plan = repository.findById(id).orElseThrow(NotFoundException::new);
//    }
}
