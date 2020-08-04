package com.prodactivv.app.core.subscription;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String price;

    private String currency;

    private Long intermittency;

    @Column(length = 10000)
    private String description;

}
