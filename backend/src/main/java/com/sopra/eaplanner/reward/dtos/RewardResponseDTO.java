package com.sopra.eaplanner.reward.dtos;

import com.sopra.eaplanner.reward.Reward;

public class RewardResponseDTO {

    private Long id;

    private Reward.Type type;

    private Integer points;

    private Long userId;

    private Integer threshold;

    public RewardResponseDTO(Reward reward) {
        this.id = reward.getId();
        this.type = reward.getType();
        this.points = reward.getPoints();
        this.userId = reward.getUser().getId();
        this.threshold = reward.getLastThreshold();
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
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Integer getThreshold() {
        return threshold;
    }
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
