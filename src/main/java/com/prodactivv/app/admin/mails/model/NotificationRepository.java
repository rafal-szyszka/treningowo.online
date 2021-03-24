package com.prodactivv.app.admin.mails.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findNotificationByTextUid(String uid);

}