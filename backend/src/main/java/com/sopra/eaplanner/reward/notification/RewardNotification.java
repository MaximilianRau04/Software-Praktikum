package com.sopra.eaplanner.reward.notification;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationType;
import com.sopra.eaplanner.reward.Reward;
import jakarta.persistence.Entity;

@Entity
public class RewardNotification extends Notification {

    private Reward.Type rewardType;
    private Integer points;
    private Integer threshold;


    public RewardNotification(String title, NotificationType notificationType, Long userId, Reward.Type rewardType, Integer points, Integer threshold) {
        super(title, notificationType, userId);
        this.rewardType = rewardType;
        this.points = points;
        this.threshold = threshold;
    }

    public RewardNotification() {

    }

    public static RewardNotification create(String title, Long userId, Reward.Type rewardType, Integer points, Integer threshold) {
        return new RewardNotification(title, NotificationType.REWARD, userId, rewardType, points, threshold);
    }

    public Reward.Type getRewardType() {
        return rewardType;
    }
    public Integer getPoints() {
        return points;
    }
    public Integer getThreshold() {
        return threshold;
    }
}
