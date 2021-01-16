package com.prodactivv.app.admin.registry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemRegistryEntityRepository extends JpaRepository<SystemRegistryEntity, Long> {

    Optional<SystemRegistryEntity> findByRegKey(String regKey);

}
