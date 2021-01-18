package com.prodactivv.app.core.subscription;

import com.prodactivv.app.admin.survey.model.Questionnaire;
import lombok.*;

import javax.persistence.*;
import java.util.Optional;

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

    public Optional<Questionnaire> getDietaryQuestionnaire() {
        return Optional.ofNullable(dietaryQuestionnaire);
    }

    public Optional<Questionnaire> getTrainingQuestionnaire() {
        return Optional.ofNullable(trainingQuestionnaire);
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
}
