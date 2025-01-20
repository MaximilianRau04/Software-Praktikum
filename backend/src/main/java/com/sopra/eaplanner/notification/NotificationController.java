package com.sopra.eaplanner.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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
        Random r = new Random();
        int d = r.nextInt(0,4);
        if(d == 0){
            String title = "Testing for Deniz";
            LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(1);
            notificationService.createAndSendEventReminder(title, eventDateTime, userId, 15L);
            return;
        } else if(d == 1){
            String title = "Test for Maxi";
            LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(10);
            notificationService.createAndSendEventReminder(title, eventDateTime, userId, 15L);
            return;
        } else if(d == 2){
            String title = "Test for Voriel";
            LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(3600);
            notificationService.createAndSendEventReminder(title, eventDateTime, userId, 15L);
            return;
        } else if(d == 3){
            String title = "Last Possibility";
            LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(600);
            notificationService.createAndSendEventReminder(title, eventDateTime, userId, 15L);
            return;
        }
        String title = "Test Notification";
        LocalDateTime eventDateTime = LocalDateTime.now().plusMinutes(1);
        notificationService.createAndSendEventReminder(title, eventDateTime, userId, 15L);
    }
}
