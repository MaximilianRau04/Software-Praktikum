package com.sopra.eaplanner.event.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventReminderNotification extends Notification {

    private LocalDateTime eventDateTime;
    private Long eventId;

    public EventReminderNotification() {
    }

    public EventReminderNotification(String title, NotificationType type, LocalDateTime eventDateTime, Long userId, Long eventId) {
        super(title, type, userId);
        this.eventDateTime = eventDateTime;
        this.eventId = eventId;
    }

    public static EventReminderNotification create(String title, LocalDateTime eventDateTime, Long userId, Long eventId) {
        return new EventReminderNotification(title, NotificationType.EVENT_REMINDER, eventDateTime, userId, eventId);
    }

    public LocalDateTime getEventDateTime() {
        return eventDateTime;
    }

    public Long getEventId(){ return eventId; }
}
