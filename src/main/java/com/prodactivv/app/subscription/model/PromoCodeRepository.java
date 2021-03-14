package com.prodactivv.app.subscription.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    @Query("SELECT pc FROM PromoCode pc WHERE pc.isActive = true")
    List<PromoCode> findAllActivePromoCodes();

    @Query("SELECT pc FROM PromoCode pc WHERE pc.code = ?1")
    Optional<PromoCode> findByCode(String code);

}
