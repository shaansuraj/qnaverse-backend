package com.qnaverse.QnAverse.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qnaverse.QnAverse.models.Notification;
import com.qnaverse.QnAverse.models.User;

/**
 * Repository for managing Notification database operations.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndReadStatusFalseOrderByCreatedAtDesc(User user);
}
