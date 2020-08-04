package com.prodactivv.app.subscription;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.subscription.SubscriptionPlan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/subscriptions")
public class SubscriptionPlanController {

    private final SubscriptionPlanService service;

    public SubscriptionPlanController(SubscriptionPlanService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SubscriptionPlan> getSingle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getSubscriptionPlanById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SubscriptionPlan> deleteSingle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.deleteSubscriptionPlanById(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<SubscriptionPlan> create(@RequestBody SubscriptionPlan subscriptionPlan) {
        return ResponseEntity.ok(service.create(subscriptionPlan));
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<SubscriptionPlan> edit(@RequestBody SubscriptionPlan subscriptionPlan) {
        return ResponseEntity.ok(service.create(subscriptionPlan));
    }

}
