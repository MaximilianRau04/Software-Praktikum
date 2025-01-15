package com.sopra.eaplanner.notification;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a notification in the system.
 * This class stores information about notifications that are sent to users regarding various events.
 * A notification includes details such as the title, description, type, user recipient, and whether it has been read.
 *
 * The `Notification` entity is persisted in the database and can be retrieved and modified using standard JPA operations.
 *
 * Notifications are linked to specific users (via `userId`) and can be marked as read (`isRead`).
 *
 * The class includes a handler (`NotificationHandler`) to process notifications based on their type. The handler is
 * defined dynamically at runtime and determines the processing logic based on the notification's type.
 *
 * Each notification is timestamped with creation and update times, allowing the system to track when the notification was created
 * and last modified.
 *
 * The entity supports different notification types (e.g., event reminders) and can be extended for future types.
 *
 * The `processNotification()` method triggers the processing of the notification through the associated handler.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Notification_Type", discriminatorType = DiscriminatorType.STRING)
public abstract class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType type;

    @NotNull
    private Boolean isRead = false;

    @NotNull
    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private NotificationHandler handler;

    public Notification(String title, NotificationType type, Long userId) {
        this.title = title;
        this.type = type;
        this.userId = userId;
    }

    public Notification() {}

    public NotificationHandler getHandler() {
        return handler;
    }

    public void setHandler(NotificationHandler handler) {
        this.handler = handler;
    }

    public @NotNull Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public @NotNull Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(@NotNull Boolean read) {
        isRead = read;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void processNotification() {
        if (handler != null) {
            handler.process(this);
        } else {
            throw new IllegalStateException("No handler defined for this notification type.");
        }
    }

    public void saveNotification(){
        if(handler != null){
            handler.saveToDatabase(this);
        } else{
            throw new IllegalStateException("No handler defined for this notification type.");
        }
    }
}
