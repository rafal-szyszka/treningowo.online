package com.prodactivv.app.user.model;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import lombok.*;
import org.apache.tomcat.jni.Local;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

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

}
