package com.sopra.eaplanner.exchangeday.dtos;

import com.sopra.eaplanner.exchangeday.ExchangeDay;

import java.time.LocalDate;

public class ExchangeDayResponseDTO {

    private Long id;
    private LocalDate date;
    private String name;
    private String location;
    private String description;

    public ExchangeDayResponseDTO() {
    }

    public ExchangeDayResponseDTO(Long id, LocalDate date, String name, String location, String description) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public ExchangeDayResponseDTO(ExchangeDay exchangeDay) {
        this.id = exchangeDay.getId();
        this.date = exchangeDay.getDate();
        this.name = exchangeDay.getName();
        this.location = exchangeDay.getLocation();
        this.description = exchangeDay.getDescription();
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
