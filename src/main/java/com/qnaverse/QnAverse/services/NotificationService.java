package com.qnaverse.QnAverse.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.models.Notification;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.repositories.NotificationRepository;
import com.qnaverse.QnAverse.repositories.UserRepository;

/**
 * Handles notification-related business logic.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a notification for a user.
     */
    public void createNotification(String username, String message) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {
            Notification notification = new Notification(user, message);
            notificationRepository.save(notification);
        });
    }

    /**
     * Fetches all unread notifications for a user.
     */
    public ResponseEntity<List<Notification>> getUnreadNotifications(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(notificationRepository.findByUserAndReadStatusFalseOrderByCreatedAtDesc(userOptional.get()));
    }

    /**
     * Marks all notifications as read for a user.
     */
    public ResponseEntity<?> markNotificationsAsRead(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Notification> notifications = notificationRepository.findByUserAndReadStatusFalseOrderByCreatedAtDesc(userOptional.get());
        notifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok("All notifications marked as read.");
    }
}
