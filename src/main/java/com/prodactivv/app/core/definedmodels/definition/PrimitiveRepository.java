package com.prodactivv.app.core.definedmodels.definition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrimitiveRepository extends JpaRepository<Primitive, Long> {
}
