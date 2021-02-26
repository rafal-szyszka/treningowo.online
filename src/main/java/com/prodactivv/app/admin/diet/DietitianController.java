package com.prodactivv.app.admin.diet;

import com.prodactivv.app.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/dietitian")
public class DietitianController {

    private final DietitianService service;

    @GetMapping
    public ResponseEntity<List<User>> getDietitians() {
        return ResponseEntity.ok(service.getAllDietitians());
    }

}
