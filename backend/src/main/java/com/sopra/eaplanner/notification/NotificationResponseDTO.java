package com.sopra.eaplanner.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for representing a notification to be sent to the frontend.
 * This DTO is used to carry notification data, including its title, description, type,
 * read status, creation time, and additional contextual data that may vary depending on the
 * type of notification.
 *
 * The `NotificationResponseDTO` is constructed from a `Notification` entity, and it provides
 * a more flexible structure for transferring data, particularly when it comes to including
 * additional context related to the notification, such as event details or user-specific data.
 *
 * The `fromNotification()` method maps the fields of the `Notification` entity to this DTO
 * and uses the appropriate `NotificationHandler` to enrich the DTO with additional context
 * for notifications that require specific data (such as event reminders).
 */
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Map<String, Object> context;

    public static NotificationResponseDTO fromNotification(Notification notification, NotificationHandlerFactory handlerFactory) {
        NotificationResponseDTO dto = new NotificationResponseDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getDescription(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );

        NotificationHandler handler = handlerFactory.getNotificationHandler(notification.getType());
        if(handler != null) {
            dto.setContext(handler.getContext(notification));
        }

        return dto;
    }

    private NotificationResponseDTO(Long id, String title, String description, NotificationType type, Boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.context = new HashMap<>();
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public NotificationType getType() {
        return type;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }
    public Boolean getIsRead() {
        return isRead;
    }
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
