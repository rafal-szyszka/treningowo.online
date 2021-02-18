package com.prodactivv.app.user.service;

import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserSubscription;
import com.prodactivv.app.core.user.UserSubscriptionDTO;
import com.prodactivv.app.core.user.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public UserSubscriptionDTO getUserActiveSubscriptions(User user) throws UserNotFoundException {
        UserSubscription userSubscription = repository.findAllUserSubscriptions(user.getId())
                .orElseThrow(new UserNotFoundException(user.getId()));
        return UserSubscriptionDTO.of(userSubscription);
    }
}
