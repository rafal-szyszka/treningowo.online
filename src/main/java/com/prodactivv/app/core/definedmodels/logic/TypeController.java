package com.prodactivv.app.core.definedmodels.logic;

import com.prodactivv.app.core.definedmodels.definition.TypeDCO;
import com.prodactivv.app.core.definedmodels.definition.TypeDTO;
import com.prodactivv.app.core.definedmodels.instances.TypeInstanceDTO;
import com.prodactivv.app.core.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/admin/type")
public class TypeController {

    private final TypeService service;
    private final TypeInstanceService instanceService;

    public TypeController(TypeService service, TypeInstanceService instanceService) {
        this.service = service;
        this.instanceService = instanceService;
    }

    @GetMapping
    public ResponseEntity<List<TypeDTO>> getAll() {
        return ResponseEntity.ok(
                service.getAllDefinedTypes()
                        .stream()
                        .map(TypeDTO::completeOf)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping(value = "/define")
    public ResponseEntity<TypeDTO> define(@RequestBody TypeDCO type) {
        try {
            return ResponseEntity.ok(TypeDTO.completeOf(service.defineNew(type)));
        } catch (NotFoundException e) {
            return ResponseEntity.of(Optional.empty());
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<TypeInstanceDTO> create(@RequestBody DataInputInterface instanceDCO) {
        try {
            return ResponseEntity.ok(TypeInstanceDTO.of(instanceService.create(instanceDCO)));
        } catch (NotFoundException e) {
            return ResponseEntity.of(Optional.empty());
        }
    }

}
