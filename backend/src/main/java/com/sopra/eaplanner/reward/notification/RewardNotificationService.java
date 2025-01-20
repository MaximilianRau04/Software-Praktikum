package com.sopra.eaplanner.reward.notification;

import com.sopra.eaplanner.notification.NotificationService;
import com.sopra.eaplanner.reward.Reward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardNotificationService {

    private NotificationService notificationService;

    @Autowired
    public RewardNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendRewardNotification(Reward reward){
        String title = reward.getType().toString();
        Long userId = reward.getUser().getId();
        Reward.Type type = reward.getType();
        Integer points = reward.getPoints();
        Integer threshold = reward.getLastThreshold();
        notificationService.createAndSendRewardNotification(title, userId, type, points, threshold);
    }
}
