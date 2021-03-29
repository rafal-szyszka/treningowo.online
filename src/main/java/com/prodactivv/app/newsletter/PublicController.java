package com.prodactivv.app.newsletter;

import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class PublicController {

    private final NewsletterService newsletterService;
    private final MailNotificationService notificationService;


    @PostMapping(value = "/public/newsletter/subscribe")
    public void subscribeToNewsletter(@RequestBody Newsletter.Dto.Subscription subscription) {
        try {
            newsletterService.subscribe(subscription);
        } catch (MandatoryRegulationsNotAcceptedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/public/newsletter/unsubscribe/{code}")
    public void unsubscribeToNewsletter(@PathVariable String code) {
        try {
            newsletterService.unsubscribe(code);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/public/contact")
    public void contact(@RequestBody ContactData contactData) {
        notificationService.sendContactNotification(contactData);
    }
}
