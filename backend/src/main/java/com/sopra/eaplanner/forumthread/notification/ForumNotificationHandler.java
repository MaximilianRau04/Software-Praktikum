package com.sopra.eaplanner.forumthread.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationHandler;

import java.util.Map;

public class ForumNotificationHandler implements NotificationHandler {


    @Override
    public void process(Notification notification) {

    }

    @Override
    public void saveToDatabase(Notification notification) {

    }

    @Override
    public Map<String, Object> getContext(Notification notification) {
        return NotificationHandler.super.getContext(notification);
    }
}
