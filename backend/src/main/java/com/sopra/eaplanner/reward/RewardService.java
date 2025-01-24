package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.reward.notification.RewardNotificationService;
import com.sopra.eaplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private RewardNotificationService rewardNotificationService;

    private static final List<Integer> ATTENDANCE_REWARD_THRESHOLDS = List.of(250, 500, 1000, 2500, 5000);
    private static final List<Integer> FEEDBACK_GIVER_REWARD_THRESHOLDS = List.of(500, 1000, 2000, 5000, 10000);

    public void createAttendanceAndFeedbackRewards(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.ATTENDER || reward.getType() == Reward.Type.FEEDBACK_GIVER) {
                return;
            }
        }
        final Integer DEFAULT_POINT_VALUE = 0;
        Reward attendanceReward = new Reward(Reward.Type.ATTENDER, DEFAULT_POINT_VALUE, "badge png path", user);
        Reward feedbackReward = new Reward(Reward.Type.FEEDBACK_GIVER, DEFAULT_POINT_VALUE, "badge png path", user);
        rewardRepository.save(attendanceReward);
        rewardRepository.save(feedbackReward);
    }

    public void grantAttendancePoints(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.ATTENDER) {
                final Integer DEFAULT_ATTENDANCE_REWARD_VALUE = 50;
                reward.setPoints(reward.getPoints() + DEFAULT_ATTENDANCE_REWARD_VALUE);
                updateRewardLevel(reward, ATTENDANCE_REWARD_THRESHOLDS);
                rewardRepository.save(reward);
            }
        }
    }

    public void grantFeedbackGiverPoints(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.FEEDBACK_GIVER) {
                final Integer DEFAULT_FEEDBACK_REWARD_VALUE = 100;
                reward.setPoints(reward.getPoints() + DEFAULT_FEEDBACK_REWARD_VALUE);
                updateRewardLevel(reward, FEEDBACK_GIVER_REWARD_THRESHOLDS);
                rewardRepository.save(reward);
            }
        }
    }

    private void updateRewardLevel(Reward reward, List<Integer> thresholds) {
        Integer currentPoints = reward.getPoints();
        Integer lastThreshold = reward.getLastThreshold();

        for (Integer threshold : thresholds) {
            if (currentPoints >= threshold && threshold > lastThreshold) {
                reward.setLastThreshold(threshold);
                rewardNotificationService.sendRewardNotification(rewardRepository.save(reward));
            }
        }
    }

    public Resource getBadgePNG(Reward.Type type, Integer threshold) {
        String fileName = type.name().toLowerCase() + "_" + threshold + ".png";
        String filePath = "rewardBadges/" + fileName;

        ClassPathResource resource = new ClassPathResource(filePath);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Badge file not found for type: " + type + " and threshold: " + threshold);
        }

        return resource;
    }
}
