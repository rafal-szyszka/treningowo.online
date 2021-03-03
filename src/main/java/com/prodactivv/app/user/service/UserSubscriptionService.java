package com.prodactivv.app.user.service;

import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserSubscriptionRepository repository;

    public UserSubscriptionDTO subscribe(User user, SubscriptionPlan plan, LocalDate until) {
        return UserSubscriptionDTO.of(
                repository.save(
                        UserSubscription.builder()
                                .plan(plan)
                                .isActive(true)
                                .user(user)
                                .until(until)
                                .build()
                )
        );
    }

    public Optional<UserSubscriptionDTO> getUserActiveSubscriptions(User user) {
        try {
            UserSubscription userSubscription = repository.findAllUserSubscriptions(user.getId())
                    .orElseThrow(new UserNotFoundException(user.getId()));
            return Optional.of(UserSubscriptionDTO.of(userSubscription));
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<UserSubscriptionDTO> getUserActiveSubscriptions(UserDTO user) {
        try {
            UserSubscription userSubscription = repository.findAllUserSubscriptions(user.getId())
                    .orElseThrow(new UserNotFoundException(user.getId()));
            return Optional.of(UserSubscriptionDTO.of(userSubscription));
        } catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }
}
