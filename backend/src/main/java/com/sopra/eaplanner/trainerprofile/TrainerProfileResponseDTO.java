package com.sopra.eaplanner.trainerprofile;

public class TrainerProfileResponseDTO {

    private Long id;
    private Long userId;
    private String bio;
    private Double averageRating;

    public TrainerProfileResponseDTO() {
    }

    public TrainerProfileResponseDTO(Long id, Long userId, String bio, Double averageRating) {
        this.id = id;
        this.bio = bio;
        this.averageRating = averageRating;
    }

    public TrainerProfileResponseDTO(TrainerProfile trainerProfile) {
        this.id = trainerProfile.getId();
        this.userId = trainerProfile.getUser().getId();
        this.bio = trainerProfile.getBio();
        this.averageRating = trainerProfile.getAverageRating();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public static TrainerProfileResponseDTO mockWith(Long id, Long userId, String bio, Double averageRating) {
        return new TrainerProfileResponseDTO(id, userId, bio, averageRating);
    }
}
