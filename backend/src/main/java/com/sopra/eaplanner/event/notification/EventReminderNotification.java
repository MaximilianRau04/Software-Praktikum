package com.sopra.eaplanner.event.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventReminderNotification extends Notification {

    private LocalDateTime eventDateTime;

    public EventReminderNotification() {
    }

    public EventReminderNotification(String title, NotificationType type, LocalDateTime eventDateTime, Long userId) {
        super(title, type, userId);
        this.eventDateTime = eventDateTime;
    }

    public static EventReminderNotification create(String title, LocalDateTime eventDateTime, Long userId) {
        return new EventReminderNotification(title, NotificationType.EVENT_REMINDER, eventDateTime, userId);
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }
}
