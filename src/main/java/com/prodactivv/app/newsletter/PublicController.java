package com.prodactivv.app.newsletter;

import com.prodactivv.app.admin.mails.MailNotificationService;
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
    public void subscribeToNewsletter(@RequestParam String email) {
        newsletterService.subscribe(email);
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
