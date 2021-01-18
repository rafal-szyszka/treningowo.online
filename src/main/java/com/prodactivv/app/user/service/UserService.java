package com.prodactivv.app.user.service;

import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserRepository;
import com.prodactivv.app.core.user.UserSubscriptionDTO;
import com.prodactivv.app.subscription.SubscriptionPlanService;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    public static final String USER_NOT_FOUND_MSG = "User not found: %s";

    private final UserRepository repository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserSubscriptionService userSubscriptionService;

    public UserService(UserRepository repository, SubscriptionPlanService subscriptionPlanService, UserSubscriptionService userSubscriptionService) {
        this.repository = repository;
        this.subscriptionPlanService = subscriptionPlanService;
        this.userSubscriptionService = userSubscriptionService;
    }

    public List<UserDTO> getUsers() {
        return repository.findAllUsers()
                .stream()
                .map(this::updateAge)
                .map(UserDTO::of)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) throws NotFoundException {
        return UserDTO.of(getUser(id));
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

    public UserSubscriptionDTO subscribe(Long userId, Long planId) throws UserNotFoundException, NotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(new UserNotFoundException(userId));

        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);

        return userSubscriptionService.subscribe(
                user, plan, LocalDate.now().plusDays(plan.getIntermittency())
        );
    }

    public UserSubscriptionDTO getUserActiveSubscriptions(Long id) throws UserNotFoundException {
        User user = repository.findById(id).orElseThrow(new UserNotFoundException(id));
        return userSubscriptionService.getUserActiveSubscriptions(user);
    }

    public List<Pair<Long, String>> getPlanQuestionnaires(Long userId) throws NotFoundException, UserNotFoundException {
        UserSubscriptionDTO subscription = userSubscriptionService.getUserActiveSubscriptions(getUser(userId));

        SubscriptionPlan subPlan = subscriptionPlanService.getSubscriptionPlanById(subscription.getSubscriptions().get(0).getId());
        Optional<Questionnaire> dietaryQuestionnaire = subPlan.getDietaryQuestionnaire();
        Optional<Questionnaire> trainingQuestionnaire = subPlan.getTrainingQuestionnaire();

        List<Pair<Long, String>> ids = new ArrayList<>();

        dietaryQuestionnaire.ifPresent(questionnaire -> ids.add(Pair.of(questionnaire.getId(), questionnaire.getName())));
        trainingQuestionnaire.ifPresent(questionnaire -> ids.add(Pair.of(questionnaire.getId(), questionnaire.getName())));

        return ids;
    }
}
