package com.prodactivv.app.admin.trainer.workout;

import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan.UsersWorkoutPlanDTO;
import com.prodactivv.app.admin.trainer.models.WorkoutPlan;
import com.prodactivv.app.admin.trainer.models.repositories.UsersWorkoutPlanRepository;
import com.prodactivv.app.admin.trainer.models.repositories.WorkoutPlanRepository;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersWorkoutPlanService {

    public static final String MSG_PLAN_FOR_USER_NOT_FOUND = "Plan %s for user %s was not found!";
    private final UsersWorkoutPlanRepository repository;
    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    private final MailNotificationService mailService;


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

    public List<UsersWorkoutPlan.Dto.WorkoutPlanData> getUserWorkoutPlansData(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(UsersWorkoutPlan.Dto.WorkoutPlanData::fromWorkoutPlan)
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

    public UsersWorkoutPlanDTO activate(Long id) throws NotFoundException, MessagingException {
        UsersWorkoutPlan usersWorkoutPlan = repository.findById(id).orElseThrow(new NotFoundException(String.format("Plan %s not found", id)));
        repository.findAllByUserId(usersWorkoutPlan.getUser().getId()).forEach(plan -> {
            plan.setIsActive(false);
            repository.save(plan);
        });

        HashMap<String, String> variables = new HashMap<>();
        variables.put("{redirect.url}", "https://treningowo.online");
        mailService.sendPlanReadyNotification(usersWorkoutPlan.getUser().getEmail(), variables);

        usersWorkoutPlan.setIsActive(true);
        return UsersWorkoutPlanDTO.of(repository.save(usersWorkoutPlan));
    }

    public UsersWorkoutPlanDTO deactivate(Long id) throws NotFoundException {
        UsersWorkoutPlan usersWorkoutPlan = repository.findById(id).orElseThrow(new NotFoundException(String.format("Plan %s not found", id)));
        usersWorkoutPlan.setIsActive(false);
        return UsersWorkoutPlanDTO.of(repository.save(usersWorkoutPlan));
    }

    public String rename(Long id, String name) throws NotFoundException {
        WorkoutPlan usersWorkoutPlan = workoutPlanRepository.findById(id).orElseThrow(new NotFoundException(String.format("Plan %s not found", id)));
        usersWorkoutPlan.setName(name);
        workoutPlanRepository.save(usersWorkoutPlan);
        return usersWorkoutPlan.getName();
    }

    public UsersWorkoutPlanDTO setActiveDate(Long id, String date) throws NotFoundException {
        UsersWorkoutPlan usersWorkoutPlan = repository.findById(id).orElseThrow(new NotFoundException(String.format("Plan %s not found", id)));
        usersWorkoutPlan.setCreatedAt(LocalDate.parse(date));
        return UsersWorkoutPlanDTO.of(repository.save(usersWorkoutPlan));
    }
}
