package com.prodactivv.app.user.service;

import com.prodactivv.app.admin.survey.model.Questionnaire;
import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.security.JwtUtils;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.model.UserRepository;
import com.prodactivv.app.user.model.UserSubscriptionDTO;
import com.prodactivv.app.subscription.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public List<UserSubscriptionDTO> getUsers(String token) throws DisintegratedJwsException {
        String role = jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ROLE);
        if (role.equalsIgnoreCase(User.Roles.DIETITIAN.getRoleName())) {
            return repository.findAllUsers()
                    .stream()
                    .map(this::updateAge)
                    .map(this::userToUserSubscription)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(this::isSubscribedToPlanWithDiet)
                    .collect(Collectors.toList());
        } else {
            return repository.findAllUsers()
                    .stream()
                    .map(this::updateAge)
                    .map(this::userToUserSubscription)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
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
                user, plan, LocalDate.now().plusMonths(plan.getIntermittency())
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

    private Optional<UserSubscriptionDTO> userToUserSubscription(User user) {
        try {
            return Optional.of(userSubscriptionService.getUserActiveSubscriptions(user));
        } catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean isSubscribedToPlanWithDiet(UserSubscriptionDTO user) {
        return user.getSubscriptions().get(0).getDietaryQuestionnaire().isPresent();
    }
}
