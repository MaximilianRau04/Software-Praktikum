package com.sopra.eaplanner.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationResponseDTO> responseDTOs = notifications.stream()
                .map(notificationService::prepareNotificationDTO)
                .toList();
        return ResponseEntity.ok(responseDTOs);
    }

    @PostMapping("/send-test-notification")
    public void sendTestNotification(@RequestParam Long userId) {
        String title = "Test Notification";
        String description = "This is a test notification.";
        LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(1);
        notificationService.createAndSendEventReminder(title, description, eventDateTime, userId);
    }
}
