package com.prodactivv.app.core.user;

import com.prodactivv.app.core.subscription.SubscriptionPlan;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDTO {

    private List<SubscriptionPlan> subscriptions;
    private UserDTO user;

    public static UserSubscriptionDTO of(UserSubscription userSubscription) {
        return new UserSubscriptionDTO(
                Collections.singletonList(userSubscription.getPlan()),
                UserDTO.of(userSubscription.getUser())
        );
    }

}
