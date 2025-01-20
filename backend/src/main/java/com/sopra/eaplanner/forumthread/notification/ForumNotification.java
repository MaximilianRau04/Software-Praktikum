package com.sopra.eaplanner.forumthread.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationType;
import jakarta.persistence.Entity;

@Entity
public class ForumNotification extends Notification {

    private Long eventId;
    private Long threadId;

    private String responderName;

    public ForumNotification() {
    }

    public ForumNotification(String title, NotificationType type, Long userId, Long eventId, Long threadId, String responderName) {
        super(title, type, userId);
        this.eventId = eventId;
        this.threadId = threadId;
        this.responderName = responderName;
    }

    public static ForumNotification create(String title, Long userId, Long eventId, Long threadId, String responderName) {
        return new ForumNotification(title, NotificationType.FORUM_POST, userId, eventId, threadId, responderName);
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public String getResponderName() {
        return responderName;
    }
}
