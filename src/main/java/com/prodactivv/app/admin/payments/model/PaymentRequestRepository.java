package com.prodactivv.app.admin.payments.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {

    @Query("SELECT pr FROM PaymentRequest pr WHERE pr.token = ?1")
    Optional<PaymentRequest> findByToken(String token);
}