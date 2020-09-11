package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.trainer.models.ActivityWeek;
import com.prodactivv.app.admin.trainer.models.ActivityWeek.ActivityWeekDTO;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan;
import com.prodactivv.app.admin.trainer.models.repositories.UsersWorkoutPlanRepository;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UsersWorkoutPlanService {

    private final UsersWorkoutPlanRepository repository;
    private final UserRepository userRepository;

    public UsersWorkoutPlanService(UsersWorkoutPlanRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public UsersWorkoutPlan createUsersWorkoutPlan(Long userId, WorkoutPlan workoutPlan) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(new UserNotFoundException(userId));

        UsersWorkoutPlan plan = new UsersWorkoutPlan();
        plan.setUser(user);
        plan.setWorkoutPlan(workoutPlan);

        return repository.save(plan);
    }
}
