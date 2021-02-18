package com.prodactivv.app.user.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("SELECT u FROM UserSubscription u WHERE u.user.id = ?1 AND u.isActive = true")
    Optional<UserSubscription> findAllUserSubscriptions(Long userId);

}
