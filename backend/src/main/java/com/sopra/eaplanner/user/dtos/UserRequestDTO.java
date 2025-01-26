package com.sopra.eaplanner.user.dtos;

import com.sopra.eaplanner.user.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class UserRequestDTO {

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+", message = "Username must be alphanumeric")
    private String username;

    @Size(max = 50, message = "Firstname cannot exceed 50 characters")
    private String firstname;

    @Size(max = 50, message = "Lastname cannot exceed 50 characters")
    private String lastname;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must be specified")
    private User.Role role;

    private Set<String> interestTags = new HashSet<>();

    public UserRequestDTO() {
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public String getDescription() {
        return description;
    }

    public User.Role getRole() {
        return role;
    }

    private Set<String> getInterestTags() {
        return interestTags;
    }

    public static UserRequestDTO mockWith(String username, String firstname, String lastname, User.Role role) {
        return new UserRequestDTO(username, firstname, lastname, role);
    }

    private UserRequestDTO(String username, String firstname, String lastname, User.Role role) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }
}