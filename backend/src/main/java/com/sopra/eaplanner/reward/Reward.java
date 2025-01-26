package com.sopra.eaplanner.reward;

import com.sopra.eaplanner.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Reward {

    public static final List<Integer> ATTENDANCE_REWARD_THRESHOLDS = List.of(250, 500, 1000);
    public static final List<Integer> FEEDBACK_GIVER_REWARD_THRESHOLDS = List.of(500, 1000, 2000);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Type {
        ATTENDER, FEEDBACK_GIVER,CLEAN_SUBMITTER, ALLROUNDER, SOCIAL_BUTTERFLY
    }

    private Type type;

    private Integer points;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    private Integer lastThreshold = 0;

    private boolean levelBased = true;
    private Integer currentLevel = 0;

    public Reward() {
    }

    public Reward(Type type, Integer points, String description, User user, boolean levelBased) {
        this.type = type;
        this.points = points;
        this.description = description;
        this.user = user;
        this.levelBased = levelBased;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String badge) {
        this.description = badge;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getLastThreshold() {
        return lastThreshold;
    }

    public void setLastThreshold(Integer lastThreshold) {
        this.lastThreshold = lastThreshold;
    }

    public boolean isLevelBased() {
        return levelBased;
    }

    public void setLevelBased(boolean levelBased) {
        this.levelBased = levelBased;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getPointsToNextLevel(Type type) {
        List<Integer> thresholds;

        switch (type) {
            case ATTENDER:
                thresholds = ATTENDANCE_REWARD_THRESHOLDS;
                break;
            case FEEDBACK_GIVER:
                thresholds = FEEDBACK_GIVER_REWARD_THRESHOLDS;
                break;
            default:
                return null; // rewards without a level are mapped to null since there are no points to gain.
        }

        if (!levelBased || currentLevel >= thresholds.size()) {
            return null;
        }
        Integer nextThreshold = thresholds.get(currentLevel);
        return Math.max(0, nextThreshold - points);
    }
}
