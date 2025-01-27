package com.sopra.eaplanner.reward.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationType;
import com.sopra.eaplanner.reward.Reward;
import jakarta.persistence.Entity;

@Entity
public class RewardNotification extends Notification {

    private Reward.Type rewardType;
    private Integer currentLevel;
    private Boolean levelBased;

    public RewardNotification(String title, NotificationType notificationType, Long userId, Reward.Type rewardType, Integer currentLevel, Boolean levelBased) {
        super(title, notificationType, userId);
        this.rewardType = rewardType;
        this.currentLevel = currentLevel;
        this.levelBased = levelBased;
    }

    public RewardNotification() {

    }

    public static RewardNotification create(String title, Long userId, Reward.Type rewardType, Integer currentLevel, Boolean levelBased) {
        return new RewardNotification(title, NotificationType.REWARD, userId, rewardType, currentLevel, levelBased);
    }

    public Reward.Type getRewardType() {
        return rewardType;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public Boolean getLevelBased() {
        return levelBased;
    }
}
