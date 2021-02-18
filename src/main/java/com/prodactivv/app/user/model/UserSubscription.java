package com.prodactivv.app.user.model;

import com.prodactivv.app.subscription.model.SubscriptionPlan;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

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

}
