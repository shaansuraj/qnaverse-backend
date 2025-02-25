package com.qnaverse.QnAverse.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qnaverse.QnAverse.models.Notification;
import com.qnaverse.QnAverse.services.NotificationService;

/**
 * Handles notification-related API endpoints.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Fetches all unread notifications for a user.
     */
    @GetMapping("/{username}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String username) {
        return notificationService.getUnreadNotifications(username);
    }

    /**
     * Marks all notifications as read for a user.
     */
    @PutMapping("/{username}/read")
    public ResponseEntity<?> markNotificationsAsRead(@PathVariable String username) {
        return notificationService.markNotificationsAsRead(username);
    }
}
