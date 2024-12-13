package com.sopra.eaplanner.user.dtos;

import com.sopra.eaplanner.user.User;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private User.Role role;
    private List<Long> registeredEventIds = new ArrayList<>();

    public UserDTO() {}

    public UserDTO(Long id, User user, List<Long> registeredEventIds) {
        this.id = id;
        this.username = user.getUsername();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.role = user.getRole();
        this.registeredEventIds = registeredEventIds;
    }

    public Long getId() { return id; }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public User.Role getRole() {
        return role;
    }

    public List<Long> getRegisteredEventIds() {
        return registeredEventIds;
    }

}