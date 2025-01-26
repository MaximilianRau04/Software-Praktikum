package com.sopra.eaplanner.exchangeday.dtos;

import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.locations.LocationDTO;

import java.time.LocalDate;

public class ExchangeDayResponseDTO {

    private Long id;
    private LocalDate startDate;
    private String name;
    private LocationDTO location;
    private String description;
    private LocalDate endDate;

    public ExchangeDayResponseDTO(ExchangeDay exchangeDay) {
        this.id = exchangeDay.getId();
        this.startDate = exchangeDay.getStartDate();
        this.endDate = exchangeDay.getEndDate();
        this.name = exchangeDay.getName();
        this.location = new LocationDTO(exchangeDay.getLocation());
        this.description = exchangeDay.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ExchangeDayResponseDTO mockWith(Long id, LocalDate startDate, LocalDate endDate, String name, LocationDTO location, String description) {
        return new ExchangeDayResponseDTO(id, startDate, endDate, name, location, description);
    }

    private ExchangeDayResponseDTO(Long id, LocalDate startDate, LocalDate endDate, String name, LocationDTO location, String description) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
