package com.prodactivv.app.subscription;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import com.prodactivv.app.core.subscription.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanService {

    public static final String EXCEPTION_NOT_FOUND_MSG = "Subscription plan not found. ID: %s";

    private final SubscriptionPlanRepository repository;

    public SubscriptionPlanService(SubscriptionPlanRepository repository) {
        this.repository = repository;
    }

    public SubscriptionPlan getSubscriptionPlanById(Long id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(new NotFoundException(String.format(EXCEPTION_NOT_FOUND_MSG, id)));
    }

    public SubscriptionPlan create(SubscriptionPlan subscriptionPlan) {
        return repository.save(subscriptionPlan);
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
