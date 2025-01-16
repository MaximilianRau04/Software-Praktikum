package com.sopra.eaplanner.trainerprofile;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrainerProfileRequestDTO {

    private String bio;

    private Double averageRating;

    private List<String> expertiseTags = new ArrayList<>();

    @NotNull(message = "User must be specified")
    private Long userId;

    public TrainerProfileRequestDTO() {
    }

    public TrainerProfileRequestDTO(String bio, Double averageRating, List<String> expertiseTags, Long userId) {
        this.bio = bio;
        this.averageRating = averageRating;
        this.expertiseTags = expertiseTags;
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getExpertiseTags() { return expertiseTags; }

    public void setExpertiseTags(List<String> expertiseTags) { this.expertiseTags = expertiseTags; }

    public static TrainerProfileRequestDTO mockWith(String bio, Double averageRating, List<String> expertiseTags, Long userId) {
        return new TrainerProfileRequestDTO(bio, averageRating, expertiseTags,  userId);
    }
}
