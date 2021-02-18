package com.prodactivv.app.user.controller;

import com.prodactivv.app.user.model.User;
import com.prodactivv.app.user.model.UserDTO;
import com.prodactivv.app.user.service.RegistrationService;
import com.prodactivv.app.user.service.UserRegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/public")
public class RegistrationController {

    private final RegistrationService service;

    @Autowired
    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<UserDTO> signUp(@RequestBody User user) {
        try {
            return ResponseEntity.ok(service.signUp(user));
        } catch (UserRegistrationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e
            );
        }
    }

}
