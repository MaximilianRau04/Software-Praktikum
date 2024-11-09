package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 16, message = "Username must be between 3 and 16 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+", message = "Username must be alphanumeric")
    private String username;

    @Size(max = 50, message = "Surname cannot exceed 50 characters")
    private String surname;

    @Size(max = 50, message = "Lastname cannot exceed 50 characters")
    private String lastname;

    public enum Role {
        ADMIN, USER
    }

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must be specified")
    private Role role;

    @ManyToMany(targetEntity = Event.class)
    private List<Event> registeredEvents = new ArrayList<>();

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(List<Event> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

    public void addEvent(Event event) {
        registeredEvents.add(event);
    }

    public void removeEvent(Event event) {
        registeredEvents.remove(event);
    }

    public void updateInformation(User user){
        if(user.getSurname() != null){
            setSurname(user.getSurname());
        }
        if(user.getLastname() != null){
            setLastname(user.getLastname());
        }
        if(user.getRole() != null){
            setRole(user.getRole());
        }
    }
}
