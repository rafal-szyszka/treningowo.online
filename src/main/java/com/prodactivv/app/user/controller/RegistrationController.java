package com.prodactivv.app.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prodactivv.app.admin.mails.MailNotificationService;
import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.core.exceptions.MandatoryRegulationsNotAcceptedException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/public")
public class RegistrationController {

    @Value("${app.homePage.url}")
    private String homePage;

    private final RegistrationService service;
    private final MailNotificationService mailService;

    @PostMapping(value = "/sign-up")
    public ResponseEntity<User.Dto.Simple> signUp(@RequestBody User.Dto.UserRegistration user) {
        try {
            User.Dto.Simple registered = service.signUp(user);
            HashMap<String, String> variables = new HashMap<>();
            variables.put("{redirect.url.profile}", homePage);
            mailService.sendWelcomeMessage(registered.getEmail(), variables);
            return ResponseEntity.ok(registered);
        } catch (UserRegistrationException | MessagingException | NotFoundException | MandatoryRegulationsNotAcceptedException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/checkCode")
    public ResponseEntity<?> checkToken(@RequestParam Long planId, @RequestParam String code) {
        try {
            return ResponseEntity.ok(service.checkPromoCodeForPlan(planId, code));
        } catch (NotFoundException e) {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping(value = "/sppRequest")
    public ResponseEntity<String> sppRequest(@RequestParam Long userId, @RequestParam(required = false) Long planId, @RequestParam(required = false) String code) {
        try {
            return ResponseEntity.ok(service.createSubRequestToken(userId, Optional.ofNullable(planId), Optional.ofNullable(code)));
        } catch (NotFoundException | MessagingException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @PutMapping(value = "/sppRequest/{token}")
    public ResponseEntity<String> sppRequest(@PathVariable String token, @RequestParam(required = false) Long planId, @RequestParam(required = false) String code) {
        try {
            return ResponseEntity.ok(service.updatePaymentRequest(token, Optional.ofNullable(planId), Optional.ofNullable(code)));
        } catch (NotFoundException | JsonProcessingException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/sppRequest/{token}")
    public ResponseEntity<PaymentRequest.Dto.Information> getPaymentInformation(@PathVariable String token) {
        try {
            return ResponseEntity.ok(service.getPaymentRequestInformation(token));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PutMapping(value = "/changePassword")
    public void sendChangePasswordMessage(@RequestParam String email) {
        try {
            service.sendChangePasswordMessage(email);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/changePassword/{hash}")
    public ResponseEntity<User.Dto.Simple> applyPasswordChange(@PathVariable String hash, @RequestBody HashMap<String, String> newPassword) {
        try {
            return ResponseEntity.ok(service.applyPasswordChange(hash, newPassword.get("pswd")));
        } catch (NotFoundException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/test")
    public void test() throws MessagingException, NotFoundException {
        mailService.sendNotificationHTML("rsonic94@gmail.com", "A", "<p>A</p>");

        HashMap<String, String> variables = new HashMap<>();
        variables.put("{redirect.url.purchaseConfirm}", "XD");

        mailService.sendRegistrationEmail("rsonic94@gmail.com", variables);
    }

}
