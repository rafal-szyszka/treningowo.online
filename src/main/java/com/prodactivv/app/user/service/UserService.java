package com.prodactivv.app.user.service;

import com.prodactivv.app.admin.survey.controller.QuestionnaireService;
import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.admin.trainer.models.UsersWorkoutPlan;
import com.prodactivv.app.admin.trainer.workout.UsersWorkoutPlanService;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.security.JwtUtils;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.subscription.service.SubscriptionPlanService;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import com.prodactivv.app.user.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    public static final String USER_NOT_FOUND_MSG = "User not found: %s";

    private final JwtUtils jwtUtils;

    private final UserRepository repository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserSubscriptionService userSubscriptionService;
    private final UsersWorkoutPlanService usersWorkoutPlanService;
    private final QuestionnaireService questionnaireService;

    public List<User.Dto.SubscriptionsAndWorkouts> getUsersWithSubscriptions(String token) throws DisintegratedJwsException {
        String demanderRole = jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ROLE);

        if (User.Roles.isDietitian(demanderRole)) {
            return repository.findAllUsers().stream()
                    .map(this::getUserWithSubscriptionsAndWorkouts)
                    .filter(User.Dto.SubscriptionsAndWorkouts::isSubscribedToPlanWithDiet)
                    .collect(Collectors.toList());
        } else {
            return repository.findAllUsers().stream()
                    .map(this::getUserWithSubscriptionsAndWorkouts)
                    .filter(User.Dto.SubscriptionsAndWorkouts::isSubscribedToPlanWithTrainings)
                    .collect(Collectors.toList());
        }
    }

    public List<User.Dto.Full> getUsersWithSubscriptions() {
        return repository.findAllUsers()
                .stream()
                .map(this::updateAge)
                .map(User.Dto.Full::fromUser)
                .collect(Collectors.toList());
    }

    public User.Dto.Full getUserById(Long id) throws NotFoundException {
        return User.Dto.Full.fromUser(getUser(id));
    }

    public User getUser(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(new NotFoundException(
                String.format(USER_NOT_FOUND_MSG, id)
        ));
    }

    private User updateAge(User user) {
        user.setAge(calculateUserAge(user));
        return repository.save(user);
    }

    public int calculateUserAge(User user) {
        int yearDifference = LocalDate.now().getYear() - user.getBirthday().getYear();
        yearDifference -= LocalDate.now().getDayOfYear() < user.getBirthday().getDayOfYear() ? 1 : 0;
        return yearDifference;
    }

    public UserSubscription.Dto.Full subscribe(Long userId, Long planId) throws UserNotFoundException, NotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(new UserNotFoundException(userId));

        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);

        return userSubscriptionService.subscribe(
                user, plan, LocalDate.now().plusMonths(plan.getIntermittency())
        );
    }

    public User.Dto.SubscriptionsAndWorkouts getUserWithSubscriptionsAndWorkouts(Long id) throws UserNotFoundException {
        User user = repository.findById(id).orElseThrow(new UserNotFoundException(id));
        return getUserWithSubscriptionsAndWorkouts(user);
    }

    public User.Dto.SubscriptionsAndWorkouts getUserWithSubscriptionsAndWorkouts(User user) {
        List<UserSubscription.Dto.FullUserLess> subscriptions = userSubscriptionService.getUserSubscriptions(user);
        List<UsersWorkoutPlan.Dto.WorkoutPlanData> userWorkoutPlansData = usersWorkoutPlanService.getUserWorkoutPlansData(user.getId());

        return User.Dto.SubscriptionsAndWorkouts.builder()
                .user(User.Dto.Full.fromUser(user))
                .subscriptions(subscriptions)
                .workoutPlans(userWorkoutPlansData)
                .build();
    }

    public List<Pair<Long, String>> getPlanQuestionnaires(Long userId) throws NotFoundException {
        User user = getUser(userId);
        List<UserSubscription.Dto.FullUserLess> userSubscriptions = userSubscriptionService.getUserSubscriptions(user);

        return userSubscriptions.stream()
                .map(UserSubscription.Dto.FullUserLess::getPlan)
                .map(this::toIdNameQuestionnaire)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Pair<Long, String>> toIdNameQuestionnaire(SubscriptionPlan.Dto.Full plan) {
        Long questionnaireId = null;
        if (plan.getDietaryQuestionnaireId().isPresent()) {
            questionnaireId = plan.getDietaryQuestionnaireId().get();
        }

        if (plan.getTrainingQuestionnaireId().isPresent()) {
            questionnaireId = plan.getTrainingQuestionnaireId().get();
        }

        if (plan.getCombinedQuestionnaireId().isPresent()) {
            questionnaireId = plan.getCombinedQuestionnaireId().get();
        }

        try {
            if (questionnaireId != null) {
                Questionnaire questionnaire = questionnaireService.getQuestionnaire(questionnaireId);
                return Optional.of(Pair.of(questionnaire.getId(), questionnaire.getName()));
            }
        } catch (NotFoundException ignored) { }

        return Optional.empty();
    }
}
