package com.prodactivv.app.newsletter;

import com.prodactivv.app.admin.mails.MailNotificationService;
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

    public void subscribe(String email) {
        LocalDate now = LocalDate.now();
        String code = hashGenerator.generateSha384Hash(Arrays.asList(now.format(DateTimeFormatter.ISO_LOCAL_DATE), email));
        newsletterRepository.save(
                Newsletter.builder()
                        .email(email)
                        .signUpDate(now)
                        .code(code)
                        .build()
        );

        notificationService.sendSubscribedToNewsletter(email, new HashMap<>());
    }

    public void unsubscribe(String code) throws NotFoundException {
        Newsletter newsletter = newsletterRepository.findNewsletterByCode(code).orElseThrow(new NotFoundException(String.format("Newsletter %s not found", code)));
        String email = newsletter.getEmail();
        newsletterRepository.delete(newsletter);

        notificationService.sendUnsubscribedToNewsletter(email, new HashMap<>());
    }
}
