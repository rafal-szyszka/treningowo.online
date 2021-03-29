package com.prodactivv.app.newsletter;

import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final HashGenerator hashGenerator;
    private final MailNotificationService notificationService;
    private final NewsletterRepository newsletterRepository;

    public void subscribe(Newsletter.Dto.Subscription subscription) throws MandatoryRegulationsNotAcceptedException {
        LocalDate now = LocalDate.now();
        String code = hashGenerator.generateSha384Hash(Arrays.asList(now.format(DateTimeFormatter.ISO_LOCAL_DATE), subscription.getEmail()));
        if (subscription.areNecessaryRegulationsAccepted()) {
            newsletterRepository.save(
                    Newsletter.builder()
                            .email(subscription.getEmail())
                            .allowedMarketingMessages(subscription.isAllowedMarketingMessages())
                            .isPrivacyPolicyAccepted(subscription.isPrivacyPolicyAccepted())
                            .isTermsOfUseAccepted(subscription.isTermsOfUseAccepted())
                            .signUpDate(now)
                            .code(code)
                            .build()
            );

            notificationService.sendSubscribedToNewsletter(subscription.getEmail(), new HashMap<>());
        } else {
            throw new MandatoryRegulationsNotAcceptedException("Mandatory regulations were not accepted!");
        }
    }

    public void unsubscribe(String code) throws NotFoundException {
        Newsletter newsletter = newsletterRepository.findNewsletterByCode(code).orElseThrow(new NotFoundException(String.format("Newsletter %s not found", code)));
        String email = newsletter.getEmail();
        newsletterRepository.delete(newsletter);

        notificationService.sendUnsubscribedToNewsletter(email, new HashMap<>());
    }
}
