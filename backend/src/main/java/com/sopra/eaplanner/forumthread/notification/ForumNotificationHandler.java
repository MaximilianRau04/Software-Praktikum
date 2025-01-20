package com.sopra.eaplanner.forumthread.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationHandler;
import com.sopra.eaplanner.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ForumNotificationHandler implements NotificationHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void process(Notification notification) {
        // potentially no uses for this specific notification
    }

    @Override
    public void saveToDatabase(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public Map<String, Object> getContext(Notification notification) {
        Map<String, Object> context = new HashMap<>();

        ForumNotification forumNotification = (ForumNotification) notification;

        context.put("eventId", forumNotification.getEventId());
        context.put("threadId", forumNotification.getThreadId());
        context.put("responderName", forumNotification.getResponderName());
        return context;
    }
}
