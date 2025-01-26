package com.sopra.eaplanner.trainerprofile;

public class TrainerProfileRequestDTO {

    private Long userId;
    private String bio;

    public TrainerProfileRequestDTO() {
    }

    public TrainerProfileRequestDTO(String bio, Long userId) {
        this.bio = bio;
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static TrainerProfileRequestDTO mockWith(String bio, Long userId) {
        return new TrainerProfileRequestDTO(bio, userId);
    }
}
