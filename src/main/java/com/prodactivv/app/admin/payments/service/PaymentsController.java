package com.prodactivv.app.admin.payments.service;

import com.prodactivv.app.admin.payments.model.PaymentRequest;
import com.prodactivv.app.admin.payments.model.PaymentVerification;
import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class PaymentsController {

    private final PaymentsService paymentsService;

    @PostMapping(value = "/public/payments/p24/verify")
    public void verifyTransaction(@RequestBody PaymentVerification verification) {
        try {
            paymentsService.receiveNotificationAndVerifyPayment(verification);
        } catch (IOException | NoSuchAlgorithmException | NotFoundException | MessagingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/public/payments/p24/methods")
    public ResponseEntity<String> getPaymentsMethods() {
        try {
            return ResponseEntity.ok(paymentsService.getPaymentMethods());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/public/payments/p24/orderPayment")
    public ResponseEntity<PaymentRequest.Dto.Confirmation> orderPayment(
            @RequestParam(name = "pt") String paymentRequestToken,
            @RequestParam(name = "m") Integer p24Method
    ) {
        try {
            return ResponseEntity.ok(paymentsService.orderPayment(paymentRequestToken, p24Method));
        } catch (NotFoundException | IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
