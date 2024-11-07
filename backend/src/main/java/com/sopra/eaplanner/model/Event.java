package com.sopra.eaplanner.model;

import java.sql.Time;

public class Event {

    private Long id;
    private String name;
    private Time startTime;
    private Time endTime;
    private String location;
    private String trainer;
    private String description;

    private Long exchangeDayId;

    public Event() {
    }

    public Event(Long id, String name, Time startTime, Time endTime, String location, String description, Long exchangeDayId) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.exchangeDayId = exchangeDayId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
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

    public Long getExchangeDayId() {
        return exchangeDayId;
    }

    public void setExchangeDayId(Long exchangeDayId) {
        this.exchangeDayId = exchangeDayId;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }
}
