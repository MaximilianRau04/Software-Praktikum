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

    public void createAttendanceAndFeedbackRewards(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.ATTENDER || reward.getType() == Reward.Type.FEEDBACK_GIVER) {
                return;
            }
        }
        final Integer DEFAULT_POINT_VALUE = 0;
        Reward attendanceReward = new Reward(Reward.Type.ATTENDER, DEFAULT_POINT_VALUE, "Belohnung für die Teilnahme an Events und Workshops.", user , true);
        Reward feedbackReward = new Reward(Reward.Type.FEEDBACK_GIVER, DEFAULT_POINT_VALUE, "Belohnung für das Hinterlassen von Bewertungen und Feedback zu besuchten Events.", user, true);
        rewardRepository.save(attendanceReward);
        rewardRepository.save(feedbackReward);
    }

    public void grantAttendancePoints(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.ATTENDER) {
                final Integer DEFAULT_ATTENDANCE_REWARD_VALUE = 50;
                reward.setPoints(reward.getPoints() + DEFAULT_ATTENDANCE_REWARD_VALUE);
                updateRewardLevel(reward, Reward.ATTENDANCE_REWARD_THRESHOLDS);
                rewardRepository.save(reward);
            }
        }
    }

    public void grantFeedbackGiverPoints(User user) {
        for (Reward reward : user.getRewards()) {
            if (reward.getType() == Reward.Type.FEEDBACK_GIVER) {
                final Integer DEFAULT_FEEDBACK_REWARD_VALUE = 100;
                reward.setPoints(reward.getPoints() + DEFAULT_FEEDBACK_REWARD_VALUE);
                updateRewardLevel(reward, Reward.FEEDBACK_GIVER_REWARD_THRESHOLDS);
                rewardRepository.save(reward);
            }
        }
    }

    private void updateRewardLevel(Reward reward, List<Integer> thresholds) {
        if (!reward.isLevelBased()) {
            return;
        }

        Integer currentPoints = reward.getPoints();
        Integer lastThreshold = reward.getLastThreshold();

        for (int level = 0; level < thresholds.size(); level++) {
            Integer threshold = thresholds.get(level);
            if (currentPoints >= threshold && threshold > lastThreshold) {
                reward.setLastThreshold(threshold);
                reward.setCurrentLevel(level + 1);
                rewardNotificationService.sendRewardNotification(rewardRepository.save(reward));
            }
        }
    }

    public Resource getBadgePNG(Reward.Type type, Integer currentLevel) {
        if(currentLevel == 0){
            ClassPathResource resource = new ClassPathResource("rewardBadges/hold.png");
            if (!resource.exists()) {
                throw new IllegalArgumentException("Badge placeholder file not found");
            }
            return resource;
        }
        String fileName = type.name().toLowerCase() + "_" + currentLevel + ".png";
        String filePath = "rewardBadges/" + fileName;

        ClassPathResource resource = new ClassPathResource(filePath);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Badge file not found for type: " + type + " and threshold: " + currentLevel);
        }

        return resource;
    }
}
