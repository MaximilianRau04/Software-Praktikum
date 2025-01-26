package com.sopra.eaplanner.reward.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationHandler;
import com.sopra.eaplanner.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RewardNotificationHandler implements NotificationHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void process(Notification notification) {
        // may not have uses yet for this NotificationType
    }

    @Override
    public void saveToDatabase(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public Map<String, Object> getContext(Notification notification) {
        Map<String, Object> context = new HashMap<>();

        RewardNotification rewardNotification = (RewardNotification) notification;

        context.put("rewardType", rewardNotification.getRewardType());
        context.put("points", rewardNotification.getPoints());
        context.put("threshold", rewardNotification.getThreshold());
        return context;
    }
}
