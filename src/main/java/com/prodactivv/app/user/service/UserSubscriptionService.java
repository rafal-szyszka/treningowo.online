package com.prodactivv.app.user.service;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserSubscription;
import com.prodactivv.app.user.model.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserSubscriptionRepository repository;

    public UserSubscription.Dto.Full subscribe(User user, SubscriptionPlan plan, LocalDate until) {
        List<UserSubscription.Dto.FullUserLess> userSubscriptions = getUserSubscriptions(user);

        if (userSubscriptions.size() > 0) {
            UserSubscription.Dto.FullUserLess lastSubscription = userSubscriptions.stream()
                    .max(Comparator.comparing(UserSubscription.Dto.FullUserLess::getUntil)).get();

            if (lastSubscription.getUntil().isAfter(LocalDate.now())) {
                until = until.plusDays(DAYS.between(LocalDate.now(), lastSubscription.getUntil()));
                return UserSubscription.Dto.Full.fromUserSubscription(
                        repository.save(
                                UserSubscription.builder()
                                        .plan(plan)
                                        .isActive(false)
                                        .bought(LocalDate.now())
                                        .user(user)
                                        .until(until)
                                        .build()
                        )
                );
            }
        }
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
