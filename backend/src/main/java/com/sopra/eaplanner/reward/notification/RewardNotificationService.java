package com.sopra.eaplanner.reward.notification;

import com.sopra.eaplanner.notification.NotificationService;
import com.sopra.eaplanner.reward.Reward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RewardNotificationService {

    private NotificationService notificationService;

    private final Map<Reward.Type, String> germanTranslations = new HashMap<Reward.Type, String>();

    @Autowired
    public RewardNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
        germanTranslations.put(Reward.Type.ATTENDER, "Teilnehmender");
        germanTranslations.put(Reward.Type.FEEDBACK_GIVER, "Feedbackgeber");
        germanTranslations.put(Reward.Type.ALLROUNDER, "Vielseitig");
        germanTranslations.put(Reward.Type.CLEAN_SUBMITTER, "Keine Halben Sachen");
        germanTranslations.put(Reward.Type.SOCIAL_BUTTERFLY, "Schmetterling");
    }

    public void sendRewardNotification(Reward reward) {
        String title = translateRewardType(reward);
        Long userId = reward.getUser().getId();
        Reward.Type type = reward.getType();
        Integer currentLevel = reward.getCurrentLevel();
        Boolean levelBased = reward.isLevelBased();
        notificationService.createAndSendRewardNotification(title, userId, type, currentLevel, levelBased);
    }

    private String translateRewardType(Reward reward) {
        return germanTranslations.get(reward.getType());
    }
}
