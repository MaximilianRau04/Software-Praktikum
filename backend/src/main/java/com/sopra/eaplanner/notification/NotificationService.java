package com.sopra.eaplanner.notification;

import com.sopra.eaplanner.event.notification.EventReminderNotification;
import com.sopra.eaplanner.sse.NotificationSseController;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing notifications. This includes creating, processing,
 * saving, marking as read, and sending notifications to users. The service interacts
 * with the `NotificationRepository` for data persistence, the `NotificationHandlerFactory`
 * for dynamic handling of different notification types, and the `NotificationSseController`
 * for sending notifications to clients via Server-Sent Events (SSE).
 * <p>
 * Key responsibilities:
 * - Preparing notification data for the frontend (via DTO).
 * - Marking notifications as read.
 * - Creating and sending event reminder notifications to users.
 * - Managing notification persistence.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationHandlerFactory notificationHandlerFactory;
    private final NotificationSseController notificationSseController;

    /**
     * Constructs a new `NotificationService` with the necessary dependencies.
     *
     * @param notificationHandlerFactory The factory for obtaining handlers for different notification types.
     * @param notificationSseController  The controller responsible for sending notifications via SSE.
     * @param notificationRepository     The repository for accessing and saving notifications in the database.
     */
    @Autowired
    public NotificationService(final NotificationHandlerFactory notificationHandlerFactory, final NotificationSseController notificationSseController, final NotificationRepository notificationRepository) {
        this.notificationHandlerFactory = notificationHandlerFactory;
        this.notificationSseController = notificationSseController;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Converts a `Notification` entity to a `NotificationResponseDTO` which is sent to the frontend.
     * The DTO is enriched with any additional context based on the notification type.
     *
     * @param notification The notification entity to be converted to a DTO.
     * @return A `NotificationResponseDTO` that contains the notification details for the frontend.
     */
    public NotificationResponseDTO prepareNotificationDTO(Notification notification) {
        return NotificationResponseDTO.fromNotification(notification, notificationHandlerFactory);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    /**
     * Marks a notification as read by updating the `isRead` flag and saving the notification in the database.
     *
     * @param notificationId The ID of the notification to be marked as read.
     * @throws EntityNotFoundException If the notification with the given ID is not found.
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Notification not found."));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * Creates and sends an event reminder notification to a user. This method generates a
     * notification based on the event details, processes it using the appropriate handler,
     * and then sends the notification to the user.
     *
     * @param title The title of the reminder notification.
     * @param eventDateTime The date and time of the event being reminded about.
     * @param userId The ID of the user to send the notification to.
     */
    public void createAndSendEventReminder(String title, LocalDateTime eventDateTime, Long userId) {
        EventReminderNotification eventReminderNotification = EventReminderNotification.create(title, eventDateTime, userId);

        NotificationHandler handler = notificationHandlerFactory.getNotificationHandler(eventReminderNotification.getType());
        eventReminderNotification.setHandler(handler);
        eventReminderNotification.processNotification();
        eventReminderNotification.saveNotification();

        notificationSseController.sendNotification(userId, eventReminderNotification);
    }
}
