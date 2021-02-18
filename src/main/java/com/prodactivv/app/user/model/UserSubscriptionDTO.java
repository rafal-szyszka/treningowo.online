package com.prodactivv.app.user.model;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

    private List<SubscriptionPlan> subscriptions;
    private LocalDate until;
    private UserDTO user;

    public static UserSubscriptionDTO of(UserSubscription userSubscription) {
        return new UserSubscriptionDTO(
                Collections.singletonList(userSubscription.getPlan()),
                userSubscription.getUntil(),
                UserDTO.of(userSubscription.getUser())
        );
    }

}
