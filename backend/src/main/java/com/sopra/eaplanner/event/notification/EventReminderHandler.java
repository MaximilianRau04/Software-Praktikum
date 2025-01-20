package com.sopra.eaplanner.event.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationHandler;
import com.sopra.eaplanner.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventReminderHandler implements NotificationHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void process(Notification notification) {
        // potentially no uses for this specific notification
    }

    @Override
    public void saveToDatabase(Notification notification) {
        notificationRepository.save(notification);
        System.out.println("Saved Event Reminder Notification: " + notification.getTitle());
    }

    @Override
    public Map<String, Object> getContext(Notification notification) {
        Map<String, Object> context = new HashMap<>();

        EventReminderNotification eventReminder = (EventReminderNotification) notification;

        context.put("eventDateTime", eventReminder.getEventDateTime());
        context.put("eventId", eventReminder.getEventId());
        return context;
    }
}
