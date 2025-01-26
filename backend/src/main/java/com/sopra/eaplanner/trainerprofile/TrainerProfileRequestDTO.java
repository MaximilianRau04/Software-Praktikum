package com.sopra.eaplanner.trainerprofile;

import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TrainerProfileRequestDTO {

    private String bio;

    private Set<String> expertiseTagNames = new HashSet<>();

    @NotNull(message = "User must be specified")
    private Long userId;

    public TrainerProfileRequestDTO() {
    }

    public TrainerProfileRequestDTO(String bio, Set<String> expertiseTagNames, Long userId) {
        this.bio = bio;
        this.expertiseTagNames = expertiseTagNames;
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

    public Set<String> getExpertiseTagNames() {
        return expertiseTagNames;
    }

    public void setExpertiseTagNames(Set<String> expertiseTagNames) {
        this.expertiseTagNames = expertiseTagNames;
    }

    public static TrainerProfileRequestDTO mockWith(String bio, Set<String> expertiseTagNames, Long userId) {
        return new TrainerProfileRequestDTO(bio, expertiseTagNames, userId);
    }
}
