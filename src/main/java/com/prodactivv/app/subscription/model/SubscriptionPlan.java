package com.prodactivv.app.subscription.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodactivv.app.admin.survey.model.Questionnaire;
import lombok.*;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;

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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "dietary_questionnaire_id", referencedColumnName = "id")
    private Questionnaire dietaryQuestionnaire;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "training_questionnaire_id", referencedColumnName = "id")
    private Questionnaire trainingQuestionnaire;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "combined_questionnaire_id", referencedColumnName = "id")
    private Questionnaire combinedQuestionnaire;

    @JsonIgnore
    @ManyToMany(mappedBy = "subscriptionPlans")
    private Set<PromoCode> promoCodes;

    public Optional<Questionnaire> getDietaryQuestionnaire() {
        return Optional.ofNullable(dietaryQuestionnaire);
    }

    public Optional<Questionnaire> getTrainingQuestionnaire() {
        return Optional.ofNullable(trainingQuestionnaire);
    }

    public Optional<Questionnaire> getCombinedQuestionnaire() {
        return Optional.ofNullable(combinedQuestionnaire);
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SubscriptionPlanDto {

        private Long id;
        private String name;
        private String price;
        private String currency;
        private Long intermittency;
        private String description;
        private Long dietaryQuestionnaireId;
        private Long trainingQuestionnaireId;
        private Long combinedQuestionnaireId;

        public static SubscriptionPlanDto of(SubscriptionPlan subscriptionPlan) {
            return SubscriptionPlanDto.builder()
                    .id(subscriptionPlan.id)
                    .name(subscriptionPlan.name)
                    .price(subscriptionPlan.price)
                    .currency(subscriptionPlan.currency)
                    .intermittency(subscriptionPlan.intermittency)
                    .description(subscriptionPlan.description)
                    .build();
        }

        public SubscriptionPlan cast() {
            return SubscriptionPlan.builder()
                    .id(id)
                    .name(name)
                    .price(price)
                    .currency(currency)
                    .intermittency(intermittency)
                    .description(description)
                    .build();
        }
    }

    public static class Dto {

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Full {
            private Long id;
            private String name;
            private String price;
            private String currency;
            private Long intermittency;
            private String description;
            private Optional<Long> dietaryQuestionnaireId;
            private Optional<Long> trainingQuestionnaireId;
            private Optional<Long> combinedQuestionnaireId;

            public static Full fromSubscriptionPlan(SubscriptionPlan plan) {

                Long dietaryQuestionnaireId = null;
                Long trainingQuestionnaireId = null;
                Long combinedQuestionnaireId = null;

                if (plan.getDietaryQuestionnaire().isPresent()) {
                    dietaryQuestionnaireId = plan.getDietaryQuestionnaire().get().getId();
                }

                if (plan.getTrainingQuestionnaire().isPresent()) {
                    trainingQuestionnaireId = plan.getTrainingQuestionnaire().get().getId();
                }

                if (plan.getCombinedQuestionnaire().isPresent()) {
                    combinedQuestionnaireId = plan.getCombinedQuestionnaire().get().getId();
                }

                return builder()
                        .id(plan.id)
                        .name(plan.name)
                        .price(plan.price)
                        .currency(plan.currency)
                        .intermittency(plan.intermittency)
                        .description(plan.description)
                        .dietaryQuestionnaireId(Optional.ofNullable(dietaryQuestionnaireId))
                        .trainingQuestionnaireId(Optional.ofNullable(trainingQuestionnaireId))
                        .combinedQuestionnaireId(Optional.ofNullable(combinedQuestionnaireId))
                        .build();
            }

            public boolean hasDietPlan() {
                return dietaryQuestionnaireId.isPresent() || combinedQuestionnaireId.isPresent();
            }

            public boolean hasTrainingPlan() {
                return trainingQuestionnaireId.isPresent() || combinedQuestionnaireId.isPresent();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class QuestionnairesLess {
            private Long id;
            private String name;
            private String price;
            private String currency;
            private Long intermittency;
            private String description;

            public static QuestionnairesLess fromSubscriptionPlan(SubscriptionPlan plan) {
                return builder()
                        .id(plan.id)
                        .name(plan.name)
                        .price(plan.price)
                        .currency(plan.currency)
                        .intermittency(plan.intermittency)
                        .description(plan.description)
                        .build();
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class FullWithQuestionnaires {
            private QuestionnairesLess plan;
            private Optional<Questionnaire> dietaryQuestionnaire;
            private Optional<Questionnaire> trainingQuestionnaire;
            private Optional<Questionnaire> combinedQuestionnaire;

            public static FullWithQuestionnaires fromSubscriptionPlan(SubscriptionPlan plan) {
                return builder()
                        .plan(QuestionnairesLess.fromSubscriptionPlan(plan))
                        .dietaryQuestionnaire(plan.getDietaryQuestionnaire())
                        .trainingQuestionnaire(plan.getTrainingQuestionnaire())
                        .combinedQuestionnaire(plan.getCombinedQuestionnaire())
                        .build();
            }

        }

    }
}
