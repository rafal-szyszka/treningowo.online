package com.prodactivv.app.subscription;

import com.prodactivv.app.admin.survey.controller.QuestionnaireService;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import com.prodactivv.app.core.subscription.SubscriptionPlan.SubscriptionPlanDto;
import com.prodactivv.app.core.subscription.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    public static final String EXCEPTION_NOT_FOUND_MSG = "Subscription plan not found. ID: %s";

    private final SubscriptionPlanRepository repository;
    private final QuestionnaireService questionnaireService;

    public SubscriptionPlan getSubscriptionPlanById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(new NotFoundException(String.format(EXCEPTION_NOT_FOUND_MSG, id)));
    }

    public SubscriptionPlan create(SubscriptionPlanDto subscriptionPlanDto) throws NotFoundException {
        SubscriptionPlan plan = subscriptionPlanDto.cast();
        if (subscriptionPlanDto.getDietaryQuestionnaireId() != null) {
            plan.setDietaryQuestionnaire(questionnaireService.getQuestionnaire(subscriptionPlanDto.getDietaryQuestionnaireId()));
        }
        if (subscriptionPlanDto.getTrainingQuestionnaireId() != null) {
            plan.setTrainingQuestionnaire(questionnaireService.getQuestionnaire(subscriptionPlanDto.getTrainingQuestionnaireId()));
        }
        return repository.save(plan);
    }

    public List<SubscriptionPlan> getAll() {
        return repository.findAll();
    }

    public SubscriptionPlan deleteSubscriptionPlanById(Long id) throws NotFoundException {
        SubscriptionPlan plan = repository.findById(id)
                .orElseThrow(new NotFoundException(String.format(EXCEPTION_NOT_FOUND_MSG, id)));

        repository.delete(plan);
        return plan;
    }
}
