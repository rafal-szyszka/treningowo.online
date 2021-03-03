package com.prodactivv.app.user.model;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.subscription.model.SubscriptionPlan.SubscriptionPlanDto;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  UserSubscriptionDTO {

    private List<SubscriptionPlan> subscriptions;
    private LocalDate until;
    private UserDTO user;
    private Long expiresInDays;

    public static UserSubscriptionDTO of(UserSubscription userSubscription) {
        return new UserSubscriptionDTO(
                Collections.singletonList(userSubscription.getPlan()),
                userSubscription.getUntil(),
                UserDTO.of(userSubscription.getUser()),
                DAYS.between(LocalDate.now(), userSubscription.getUntil())
        );
    }

    public static SimpleSubscriptionView simpleOf(UserSubscription userSubscription) {
        return SimpleSubscriptionView.of(userSubscription);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleSubscriptionView {
        private List<SubscriptionPlanDto> subscriptions;
        private LocalDate until;
        private Long expiresInDays;

        public static SimpleSubscriptionView of(UserSubscription userSubscription) {
            if (userSubscription == null) return null;
            return new SimpleSubscriptionView(
                    Collections.singletonList(SubscriptionPlanDto.of(userSubscription.getPlan())),
                    userSubscription.getUntil(),
                    DAYS.between(LocalDate.now(), userSubscription.getUntil())
            );
        }

        public static SimpleSubscriptionView of(UserSubscriptionDTO userSubscription) {
            if (userSubscription == null) return null;
            return new SimpleSubscriptionView(
                    userSubscription.subscriptions.stream().map(SubscriptionPlanDto::of).collect(Collectors.toList()),
                    userSubscription.getUntil(),
                    DAYS.between(LocalDate.now(), userSubscription.getUntil())
            );
        }
    }

}
