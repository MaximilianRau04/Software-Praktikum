package com.sopra.eaplanner.exchangeday;

import java.time.LocalDate;
import java.util.List;

public class ExchangeDayDTO {

    private Long id;
    private LocalDate date;
    private String name;
    private String location;
    private String description;
    private List<Long> eventIds;


    public ExchangeDayDTO(Long id, LocalDate date, String name, String location, String description, List<Long> eventIds) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.location = location;
        this.description = description;
        this.eventIds = eventIds;
    }

    public ExchangeDayDTO(ExchangeDay exchangeDay, List<Long> eventIds) {
        this.id = exchangeDay.getId();
        this.date = exchangeDay.getDate();
        this.name = exchangeDay.getName();
        this.location = exchangeDay.getLocation();
        this.description = exchangeDay.getDescription();
        this.eventIds = eventIds;
    }

    // Getter und Setter
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

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }
}
