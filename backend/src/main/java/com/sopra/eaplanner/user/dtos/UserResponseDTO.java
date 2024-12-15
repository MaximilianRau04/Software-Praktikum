package com.sopra.eaplanner.user.dtos;

import com.sopra.eaplanner.user.User;

public class UserResponseDTO {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private User.Role role;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public static UserResponseDTO mockWith(Long id, String username, String firstname, String lastname, User.Role role) {
        return new UserResponseDTO(id, username, firstname, lastname, role);
    }

    private UserResponseDTO(Long id, String username, String firstname, String lastname, User.Role role) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }
}
