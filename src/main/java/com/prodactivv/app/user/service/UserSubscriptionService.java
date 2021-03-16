package com.prodactivv.app.user.service;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.user.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserSubscriptionRepository repository;

    public UserSubscription.Dto.Full subscribe(User user, SubscriptionPlan plan, LocalDate until) {
        return UserSubscription.Dto.Full.fromUserSubscription(
                repository.save(
                        UserSubscription.builder()
                                .plan(plan)
                                .isActive(true)
                                .bought(LocalDate.now())
                                .user(user)
                                .until(until)
                                .build()
                )
        );
    }

    public List<UserSubscription.Dto.FullUserLess> getUserSubscriptions(User user) {
        return repository.findAllUserSubscriptions(user.getId())
                .stream()
                .map(UserSubscription.Dto.FullUserLess::fromUserSubscription)
                .collect(Collectors.toList());
    }

    public List<UserSubscription.Dto.FullUserLess> getUserSubscriptions(User.Dto.Simple user) {
        return repository.findAllUserSubscriptions(user.getId())
                .stream()
                .map(UserSubscription.Dto.FullUserLess::fromUserSubscription)
                .collect(Collectors.toList());
    }
}
