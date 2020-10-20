package com.prodactivv.app.user.service;

import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import com.prodactivv.app.core.user.User;
import com.prodactivv.app.core.user.UserSubscription;
import com.prodactivv.app.core.user.UserSubscriptionDTO;
import com.prodactivv.app.core.user.UserSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionService {

    private final UserSubscriptionRepository repository;

    public UserSubscriptionService(UserSubscriptionRepository repository) {
        this.repository = repository;
    }


    public UserSubscriptionDTO subscribe(User user, SubscriptionPlan plan, LocalDate until) {
        return UserSubscriptionDTO.of(
                repository.save(
                        UserSubscription.builder()
                                .plan(plan)
                                .user(user)
                                .until(until)
                                .build()
                )
        );
    }

    public UserSubscriptionDTO getUserActiveSubscriptions(User user) throws UserNotFoundException {
        UserSubscription userSubscription = repository.findAllUserSubscriptionsByDate(user.getId())
                .orElseThrow(new UserNotFoundException(user.getId()));
        return UserSubscriptionDTO.of(userSubscription);
    }
}
