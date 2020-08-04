package com.prodactivv.app.user.service;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserRepository;
import com.prodactivv.app.core.user.UserSubscriptionDTO;
import com.prodactivv.app.subscription.SubscriptionPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserSubscriptionService userSubscriptionService;

    public UserService(UserRepository repository, SubscriptionPlanService subscriptionPlanService, UserSubscriptionService userSubscriptionService) {
        this.repository = repository;
        this.subscriptionPlanService = subscriptionPlanService;
        this.userSubscriptionService = userSubscriptionService;
    }

    public List<UserDTO> getUsers() {
        return repository.findAll()
                .stream()
                .map(this::updateAge)
                .map(UserDTO::of)
                .collect(Collectors.toList());
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
}