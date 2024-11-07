package com.sopra.eaplanner.model;

public class User {

    private Long id;
    private String username;
    private String surname;
    private String lastname;
    private boolean isAdmin;

    public User() {
    }

    public User(Long id, String username, String surname, String lastname, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.surname = surname;
        this.lastname = lastname;
        this.isAdmin = isAdmin;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
