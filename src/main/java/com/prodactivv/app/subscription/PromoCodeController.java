package com.prodactivv.app.subscription;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.subscription.model.PromoCode;
import com.prodactivv.app.subscription.model.PromoCode.PromoCodeDto;
import com.prodactivv.app.subscription.service.PromoCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/promoCodes")
public class PromoCodeController {

    private final PromoCodeService service;

    @GetMapping
    public ResponseEntity<List<PromoCode>> getAllPromoCodes() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PromoCode> getPromoCode(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping
    public ResponseEntity<PromoCode> createPromoCode(@RequestBody PromoCodeDto promoCode) {
        return ResponseEntity.ok(service.createNewPromoCode(promoCode));
    }

    @PutMapping
    public ResponseEntity<PromoCode> editPromoCode(@RequestBody PromoCodeDto promoCode) {
        try {
            return ResponseEntity.ok(service.editPromoCode(promoCode));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
