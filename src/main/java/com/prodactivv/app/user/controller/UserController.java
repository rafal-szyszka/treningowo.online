package com.prodactivv.app.user.controller;

import com.prodactivv.app.core.exceptions.NotFoundException;
import com.prodactivv.app.core.exceptions.UserNotFoundException;
import com.prodactivv.app.core.user.UserDTO;
import com.prodactivv.app.core.user.UserSubscriptionDTO;
import com.prodactivv.app.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/admin/users/{id}")
    public ResponseEntity<UserSubscriptionDTO> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUserActiveSubscriptions(id));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

    @GetMapping(value = "/admin/users/getAll")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping(value = "/user/{userId}/subscribe/{planId}")
    public ResponseEntity<UserSubscriptionDTO> subscribe(@PathVariable Long userId, @PathVariable Long planId) {
        try {
            return ResponseEntity.ok(userService.subscribe(userId, planId));
        } catch (UserNotFoundException | NotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }
}
