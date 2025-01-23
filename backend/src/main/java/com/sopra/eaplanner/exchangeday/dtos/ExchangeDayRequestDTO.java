package com.sopra.eaplanner.exchangeday.dtos;

import com.sopra.eaplanner.locations.Location;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ExchangeDayRequestDTO {

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @FutureOrPresent(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @FutureOrPresent(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate endDate;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "LocationId cannot be null")
    private Long locationId;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    public ExchangeDayRequestDTO() {
    }

    public @Future(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate getStartDate() {
        return startDate;
    }

    public void setDate(@Future(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String getName() {
        return name;
    }

    public void setName(@NotNull(message = "Name cannot be null") @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters") String name) {
        this.name = name;
    }

    public @NotNull Long getLocationId() {
        return locationId;
    }

    public void setLocationId(@NotNull Long locationId) {
        this.locationId = locationId;
    }

    public @Size(max = 255, message = "Description cannot exceed 255 characters") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 255, message = "Description cannot exceed 255 characters") String description) {
        this.description = description;
    }

    public @Future(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@Future(message = "Date must be in the future") @NotNull(message = "Date cannot be null") LocalDate endDate) {
        this.endDate = endDate;
    }


    public static ExchangeDayRequestDTO mockWith(LocalDate startDate, LocalDate endDate, String name, Long locationId, String description) {
        return new ExchangeDayRequestDTO(startDate, endDate, name, locationId, description);
    }

    private ExchangeDayRequestDTO(LocalDate startDate, LocalDate endDate, String name, Long locationId, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.locationId = locationId;
        this.description = description;
    }
}
