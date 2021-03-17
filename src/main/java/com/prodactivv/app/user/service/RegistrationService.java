package com.prodactivv.app.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.config.P24Defaults;
import com.prodactivv.app.core.events.Event;
import com.prodactivv.app.core.events.EventService;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.security.AuthService;
import com.prodactivv.app.subscription.model.PromoCode;
import com.prodactivv.app.subscription.model.SubscriptionPlan;
import com.prodactivv.app.subscription.service.PromoCodeService;
import com.prodactivv.app.subscription.service.SubscriptionPlanService;
import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.admin.payments.model.PaymentRequest.PaymentRequestBuilder;
import com.prodactivv.app.admin.payments.model.PaymentRequestRepository;
import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PaymentRequestRepository paymentRequestRepository;

    private final UserService userService;
    private final AuthService authService;
    private final PromoCodeService promoCodeService;
    private final MailNotificationService mailService;
    private final SubscriptionPlanService subscriptionPlanService;
    private final EventService eventService;

    private final P24Defaults p24Defaults;

    @Value("${app.changePassword.url}")
    private String changePasswordRequestUrl;

    public User.Dto.Simple signUp(User.Dto.UserRegistration userRegDto) throws UserRegistrationException, MandatoryRegulationsNotAcceptedException {
        User user = userRegDto.toUser();

        if (userRegDto.getPrivacyPolicy() && userRegDto.getTermsOfUse()) {
            try {
                if (userRepository.findUserByEmail(user.getEmail()).isEmpty()) {
                    user.setSignedUpDate(LocalDate.now());
                    user.setAge(userService.calculateUserAge(user));
                    user.setRole(User.Roles.USER.getRoleName());
                    User.Dto.Simple userDto = User.Dto.Simple.fromUser(userRepository.save(user));
                    userDto.setToken(authService.generateTokenForUser(user).getToken());
                    return userDto;
                } else {
                    throw new UserRegistrationException("Email is already taken");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new UserRegistrationException(e.getMessage());
            }
        }

        throw new MandatoryRegulationsNotAcceptedException();
    }

    public String createSubRequestToken(Long userId, Optional<Long> planId, Optional<String> codeId) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(new NotFoundException(String.format("User %s not found", userId)));

        PaymentRequestBuilder paymentRequestBuilder = PaymentRequest.builder()
                .user(user)
                .p24MerchantId(p24Defaults.getMerchantId())
                .p24Crc(p24Defaults.getCrc())
                .isVerified(false)
                .isFinalized(false);

        if (planId.isPresent()) {
            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId.get());
            paymentRequestBuilder.plan(plan);

            if (codeId.isPresent()) {
                PromoCode promoCode = promoCodeService.getByCode(codeId.get());
                paymentRequestBuilder.promoCode(checkPromoCodeForPlan(plan.getId(), promoCode.getCode()) ? promoCode : null);
            }
        }

        PaymentRequest paymentRequest = paymentRequestBuilder.build();

        updateFinalPrice(paymentRequest);

        paymentRequest = paymentRequestRepository.save(paymentRequest);
        mailService.sendNotification(
                user.getEmail(),
                "Witaj",
                String.format("Twój kod do rejestracji wygasa %s. Kod: %s", paymentRequest.getValidUntil().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), paymentRequest.getToken())
        );

        return paymentRequest.getToken();
    }

    public boolean checkPromoCodeForPlan(Long planId, String code) throws NotFoundException {
        SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
        return plan.getPromoCodes().stream()
                .filter(promoCode -> promoCode.getCode().equalsIgnoreCase(code))
                .anyMatch(promoCodeService::isValid);
    }

    public String updatePaymentRequest(String token, Optional<Long> planId, Optional<String> codeId) throws NotFoundException, JsonProcessingException, NoSuchAlgorithmException {
        PaymentRequest paymentRequest = paymentRequestRepository.findByToken(token).orElseThrow(new NotFoundException(String.format("Payment request %s not found", token)));

        if (planId.isPresent()) {
            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId.get());
            paymentRequest.setPlan(plan);

            if (codeId.isPresent()) {
                PromoCode promoCode = promoCodeService.getByCode(codeId.get());
                paymentRequest.setPromoCode(checkPromoCodeForPlan(plan.getId(), promoCode.getCode()) ? promoCode : null);
            }
        }

        updateFinalPrice(paymentRequest);
        paymentRequest.recalculate(PaymentRequest.PERSIST_TOKEN);

        paymentRequest = paymentRequestRepository.save(paymentRequest);

        return paymentRequest.getToken();
    }

    public PaymentRequest.Dto.Information getPaymentRequestInformation(String token) throws NotFoundException {
        PaymentRequest paymentRequest = paymentRequestRepository.findByToken(token).orElseThrow(new NotFoundException(String.format("Payment request %s not found", token)));
        return PaymentRequest.Dto.Information.fromPaymentRequest(paymentRequest);
    }

    public void sendChangePasswordMessage(String email) throws NotFoundException {
        User user = userService.getUserByEmail(email);
        Event passwordEvent = eventService.createUserBasedEvent(EventService.EventType.CHANGE_PASSWORD, user, LocalDate.now().plusDays(7L));
        mailService.sendNotification(
                email,
                "Prośba zmiany hasła",
                "Link resetowania hasła: " + changePasswordRequestUrl + passwordEvent.getCode());
    }

    public User.Dto.Simple applyPasswordChange(String eventHash, String newPassword) throws NotFoundException, NoSuchAlgorithmException {
        Event eventByHash = eventService.getEventByHash(eventHash);
        User user = eventByHash.getUser();
        user.setPassword(newPassword);
        user.hashPassword();

        eventService.deleteEvent(eventByHash);

        mailService.sendNotification(
                user.getEmail(),
                "Hasło zmienione",
                "Twoje hasło zostało zmienione."
        );

        return User.Dto.Simple.fromUser(userRepository.save(user));
    }

    private void updateFinalPrice(PaymentRequest paymentRequest) {
        BigDecimal hundred = new BigDecimal(100L);
        if (paymentRequest.getPromoCode() != null && paymentRequest.getPlan() != null) {
            BigDecimal price = new BigDecimal(paymentRequest.getPlan().getPrice());
            BigDecimal discount = new BigDecimal(100L - paymentRequest.getPromoCode().getDiscountPercent());
            discount = discount.divide(hundred, new MathContext(2, RoundingMode.HALF_UP));
            paymentRequest.setFinalPrice(price.multiply(discount).multiply(hundred).intValue());
        } else if (paymentRequest.getPlan() != null) {
            BigDecimal price = new BigDecimal(paymentRequest.getPlan().getPrice());
            paymentRequest.setFinalPrice(price.multiply(hundred).intValue());
        } else {
            paymentRequest.setFinalPrice(0);
        }
    }
}
