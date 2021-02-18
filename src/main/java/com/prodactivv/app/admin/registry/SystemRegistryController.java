package com.prodactivv.app.admin.registry;

import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/registry")
public class SystemRegistryController {

    private final SystemRegistryService service;

    @GetMapping(value = "/{key}")
    public ResponseEntity<SystemRegistryEntity> getValueByKey(@PathVariable String key) {
        try {
            return ResponseEntity.ok(service.getEntityByKey(key));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping
    public ResponseEntity<SystemRegistryEntity> createRegistryEntity(@RequestBody SystemRegistryEntity entity) {
        return ResponseEntity.ok(service.createEntity(entity));
    }

    @PutMapping
    public ResponseEntity<SystemRegistryEntity> updateRegistryEntity(@RequestBody SystemRegistryEntity entity) {
        try {
            return ResponseEntity.ok(service.updateEntity(entity));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
