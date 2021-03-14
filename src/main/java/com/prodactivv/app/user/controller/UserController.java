package com.prodactivv.app.user.controller;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.security.JwtUtils;
import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserSubscription;
import com.prodactivv.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping(value = "/admin/users/{id}")
    public ResponseEntity<User.Dto.SubscriptionsAndWorkouts> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserWithSubscriptionsAndWorkouts(id));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/admin/users/getAll")
    public ResponseEntity<List<User.Dto.SubscriptionsAndWorkouts>> getUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.getUsersWithSubscriptions(token));
        } catch (DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/user/{userId}/subscribe/{planId}")
    public ResponseEntity<UserSubscription.Dto.Full> subscribe(@PathVariable Long userId, @PathVariable Long planId) {
        try {
            return ResponseEntity.ok(userService.subscribe(userId, planId));
        } catch (UserNotFoundException | NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/admin/user/{id}/questionnaires")
    public ResponseEntity<List<Pair<Long, String>>> getUsersSubscriptionPlanQuestionnaires(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getPlanQuestionnaires(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/user/subscription/questionnaires")
    public ResponseEntity<List<Pair<Long, String>>> getUserSubscriptionPlanQuestionnaires(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(userService.getPlanQuestionnaires(Long.valueOf(jwtUtils.obtainClaimWithIntegrityCheck(token, JwtUtils.CLAIM_ID))));
        } catch (NotFoundException | DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
