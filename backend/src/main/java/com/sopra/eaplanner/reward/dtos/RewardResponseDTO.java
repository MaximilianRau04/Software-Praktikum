package com.sopra.eaplanner.reward.dtos;

import com.sopra.eaplanner.reward.Reward;

public class RewardResponseDTO {

    private Long id;
    private Reward.Type type;
    private Integer points;
    private String description;
    private Integer currentLevel;
    private Integer pointsToNextLevel;
    private Boolean levelBased;

    public RewardResponseDTO(Reward reward) {
        this.id = reward.getId();
        this.type = reward.getType();
        this.points = reward.getPoints();
        this.description = reward.getDescription();
        this.levelBased = reward.isLevelBased();
        if(reward.isLevelBased()){
            this.currentLevel = reward.getCurrentLevel();
            this.pointsToNextLevel = reward.getPointsToNextLevel(reward.getType());
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Reward.Type getType() {
        return type;
    }
    public void setType(Reward.Type type) {
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
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }
    public Integer getPointsToNextLevel() {
        return pointsToNextLevel;
    }
    public void setPointsToNextLevel(Integer pointsToNextLevel) {
        this.pointsToNextLevel = pointsToNextLevel;
    }

    public Boolean getLevelBased() {
        return levelBased;
    }
    public void setLevelBased(Boolean levelBased) {
        this.levelBased = levelBased;
    }
}
