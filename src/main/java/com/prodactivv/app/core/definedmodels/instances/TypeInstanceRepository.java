package com.prodactivv.app.core.definedmodels.instances;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeInstanceRepository extends JpaRepository<TypeInstance, Long> {
}
