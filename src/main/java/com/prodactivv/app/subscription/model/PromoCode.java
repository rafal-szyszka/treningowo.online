package com.prodactivv.app.subscription.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private Long discountPercent;

    private LocalDate validUntil;

    private Long amountToUse;

    private Boolean isIndefinite;

    private Boolean isActive;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "subscription_plan_promo_codes",
            joinColumns = @JoinColumn(name = "promo_code_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_plan_id"))
    private Set<SubscriptionPlan> subscriptionPlans;

    public PromoCode(PromoCodeDto dto) {
        id = dto.id;
        code = dto.code;
        discountPercent = dto.discountPercent;
        validUntil = dto.validUntil;
        amountToUse = dto.amountToUse;
        isIndefinite = dto.isIndefinite;
        isActive = dto.isActive;
    }

    public void addSubscriptionPlan(SubscriptionPlan plan) {
        if (subscriptionPlans == null) {
            subscriptionPlans = new HashSet<>();
        }
        subscriptionPlans.add(plan);
    }

    public void removeSubscriptionPlan(SubscriptionPlan plan) {
        if (subscriptionPlans != null) {
            subscriptionPlans.remove(plan);
        }
    }

    public void clearSubscriptionPlans() {
        subscriptionPlans.clear();
    }

    public void updateBy(PromoCodeDto dto) {
        code = dto.code;
        discountPercent = dto.discountPercent;
        validUntil = dto.validUntil;
        amountToUse = dto.amountToUse;
        isIndefinite = dto.isIndefinite;
        isActive = dto.isActive;
    }

    public boolean isIndefinite() {
        return isActive && validUntil == null;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PromoCodeDto {

        private Long id;
        private String code;
        private Long discountPercent;
        private LocalDate validUntil;
        private Long amountToUse;
        private Boolean isIndefinite;
        private Boolean isActive;
        private List<Long> subscriptionPlans;

    }

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Details {
            private Long id;
            private String code;
            private Long discountPercent;

            public static Details fromPromoCode(PromoCode promoCode) {
                return builder()
                        .id(promoCode.id)
                        .code(promoCode.code)
                        .discountPercent(promoCode.discountPercent)
                        .build();
            }
        }

    }
}