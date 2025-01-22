package com.sopra.eaplanner.trainerprofile;

public class TrainerProfileResponseDTO {

    private long id;
    private String bio;
    private Double averageRating;

    public TrainerProfileResponseDTO() {
    }

    public TrainerProfileResponseDTO(Long id, String bio, Double averageRating) {
        this.id = id;
        this.bio = bio;
        this.averageRating = averageRating;
    }

    public TrainerProfileResponseDTO(TrainerProfile trainerProfile) {
        this.id = trainerProfile.getId();
        this.bio = trainerProfile.getBio();
        this.averageRating = trainerProfile.getAverageRating();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public static TrainerProfileResponseDTO mockWith(Long id, String bio, Double averageRating) {
        return new TrainerProfileResponseDTO(id, bio, averageRating);
    }
}
