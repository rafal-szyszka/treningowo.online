package com.prodactivv.app.admin.registry;

import com.prodactivv.app.core.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemRegistryService {

    private final SystemRegistryEntityRepository repository;

    public SystemRegistryEntity createEntity(SystemRegistryEntity entity) {
        return repository.save(entity);
    }

    public SystemRegistryEntity getEntityByKey(String key) throws NotFoundException {
        return repository.findByRegKey(key).orElseThrow(new NotFoundException(String.format("%s not found!", key)));
    }

    public SystemRegistryEntity updateEntity(SystemRegistryEntity entity) {
        return createEntity(entity);
    }
}
