package com.sopra.eaplanner.trainerprofile;

import java.util.ArrayList;
import java.util.List;

public class TrainerProfileResponseDTO {

    private long id;
    private String bio;
    private Double averageRating;
    private List<String> expertiseTags = new ArrayList<>();

    public TrainerProfileResponseDTO() {
    }

    public TrainerProfileResponseDTO(Long id, String bio, Double averageRating, List<String> expertiseTags) {
        this.id = id;
        this.bio = bio;
        this.averageRating = averageRating;
        this.expertiseTags = expertiseTags;
    }

    public TrainerProfileResponseDTO(TrainerProfile trainerProfile) {
        this.id = trainerProfile.getId();
        this.bio = trainerProfile.getBio();
        this.averageRating = trainerProfile.getAverageRating();
        this.expertiseTags = trainerProfile.getExpertiseTags();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public List<String> getExpertiseTags() { return expertiseTags; }
    public void setExpertiseTags(List<String> expertiseTags) { this.expertiseTags = expertiseTags; }

    public static TrainerProfileResponseDTO mockWith(Long id, String bio, Double averageRating, List<String> expertiseTags) {
        return new TrainerProfileResponseDTO(id, bio, averageRating, expertiseTags);
    }
}
