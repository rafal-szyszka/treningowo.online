package com.prodactivv.app.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/public")
public class RegistrationController {

    private final RegistrationService service;

    @Autowired
    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<User.Dto.Full> signUp(@RequestBody User.Dto.UserRegistration user) {
        try {
            return ResponseEntity.ok(service.signUp(user));
        } catch (UserRegistrationException e) {
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
        } catch (NotFoundException e) {
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

}
