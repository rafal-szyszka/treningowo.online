package com.prodactivv.app.core.security;

import com.prodactivv.app.core.exceptions.DisintegratedJwsException;
import com.prodactivv.app.core.exceptions.ExceptionResponseWrapper;
import com.prodactivv.app.core.exceptions.InvalidCredentialsException;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/public/auth/create")
    public ResponseEntity<AuthResponse> generateToken(@RequestBody String userKey) {
        try {
            return ResponseEntity.ok(authService.generateToken(userKey));
        } catch (NotFoundException | InvalidCredentialsException | DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping(value = "/user/auth/info")
    public ResponseEntity<AuthResponse> getUserData(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(authService.getTokenData(token));
        } catch (NotFoundException | DisintegratedJwsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
