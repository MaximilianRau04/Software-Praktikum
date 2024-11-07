package com.sopra.eaplanner.model;

import java.time.LocalDate;

public class ExchangeDay {

    private Long id;
    private LocalDate date;
    private String name;
    private String location;
    private String description;

    public ExchangeDay() {
    }

    public ExchangeDay(Long id, LocalDate date, String name, String location, String description) {
        this.name = name;
        this.id = id;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}