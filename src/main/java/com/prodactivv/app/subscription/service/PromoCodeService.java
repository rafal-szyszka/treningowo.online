package com.prodactivv.app.subscription.service;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.subscription.model.PromoCode;
import com.prodactivv.app.subscription.model.PromoCode.PromoCodeDto;
import com.prodactivv.app.subscription.model.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository repository;
    private final SubscriptionPlanService subscriptionPlanService;

    public List<PromoCode> getAll() {
        return repository.findAll().stream()
                .map(this::updateStatus)
                .collect(Collectors.toList());
    }

    private PromoCode updateStatus(PromoCode promoCode) {
        if (!isValid(promoCode)) {
            promoCode.setIsActive(false);
            return repository.save(promoCode);
        }

        return promoCode;
    }

    public PromoCode getById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(new NotFoundException(String.format("Promo code %s not found", id)));
    }

    public PromoCode getByCode(String code) throws NotFoundException {
        return repository.findByCode(code).orElseThrow(new NotFoundException(String.format("Promo code %s not found", code)));
    }

    public PromoCode createNewPromoCode(PromoCodeDto promoCode) {
        PromoCode code = new PromoCode(promoCode);
        promoCode.getSubscriptionPlans().stream()
                .map(subscriptionPlanService::getSubscriptionPlanByIdNullSafe)
                .filter(Optional::isPresent)
                .forEach(plan -> code.addSubscriptionPlan(plan.get()));

        return repository.save(code);
    }

    public PromoCode editPromoCode(PromoCodeDto promoCode) throws NotFoundException {
        PromoCode code = getById(promoCode.getId());
        code.clearSubscriptionPlans();

        promoCode.getSubscriptionPlans().stream()
                .map(subscriptionPlanService::getSubscriptionPlanByIdNullSafe)
                .filter(Optional::isPresent)
                .forEach(plan -> code.addSubscriptionPlan(plan.get()));

        code.updateBy(promoCode);

        return repository.save(code);
    }

    public PromoCode deleteById(Long id) throws NotFoundException {
        PromoCode promoCode = getById(id);

        promoCode.clearSubscriptionPlans();

        repository.delete(promoCode);
        return promoCode;
    }

    public PromoCode activateCode(Long id) throws NotFoundException {
       return managePromoCodeState(id, true);
    }

    public PromoCode deactivateCode(Long id) throws NotFoundException {
        return managePromoCodeState(id, false);
    }

    private PromoCode managePromoCodeState(Long id, Boolean state) throws NotFoundException {
        PromoCode promoCode = repository.findById(id).orElseThrow(new NotFoundException(String.format("Promo code %s not found.", id)));
        promoCode.setIsActive(state);
        return repository.save(promoCode);
    }

    public boolean isValid(PromoCode promoCode) {
        return promoCode.getIsActive() && (promoCode.isIndefinite() || (promoCode.getIsActive() && DAYS.between(LocalDate.now(), promoCode.getValidUntil()) >= 0));
    }
}
