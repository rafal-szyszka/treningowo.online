package com.prodactivv.app.user.model;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate until;

    @Column(columnDefinition = "boolean DEFAULT true")
    private Boolean isActive;

    private LocalDate bought;

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor(access = AccessLevel.PROTECTED)
        public static class FullUserLess {
            private Long id;
            private Boolean isActive;
            private Long daysLeft;
            private LocalDate until;
            private LocalDate bought;
            private SubscriptionPlan.Dto.Full plan;

            public static FullUserLess fromUserSubscription(UserSubscription userSubscription) {
                return builder()
                        .id(userSubscription.id)
                        .isActive(userSubscription.until.isAfter(LocalDate.now()))
                        .daysLeft(DAYS.between(LocalDate.now(), userSubscription.until))
                        .until(userSubscription.until)
                        .plan(SubscriptionPlan.Dto.Full.fromSubscriptionPlan(userSubscription.plan))
                        .bought(userSubscription.bought)
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Full {
            private FullUserLess subscription;
            private User.Dto.Simple user;

            public static Full fromUserSubscription(UserSubscription userSubscription) {
               return builder()
                       .subscription(FullUserLess.fromUserSubscription(userSubscription))
                       .user(User.Dto.Simple.fromUser(userSubscription.user))
                       .build();
            }
        }

    }

}
