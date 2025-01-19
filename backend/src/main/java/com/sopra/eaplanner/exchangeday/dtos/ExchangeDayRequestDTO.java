package com.sopra.eaplanner.exchangeday.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ExchangeDayRequestDTO {

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Future(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Future(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate endDate;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    private String location;

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

    public @NotNull @NotEmpty String getLocation() {
        return location;
    }

    public void setLocation(@NotNull @NotEmpty String location) {
        this.location = location;
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


    public static ExchangeDayRequestDTO mockWith(LocalDate startDate, LocalDate endDate, String name, String location, String description) {
        return new ExchangeDayRequestDTO(startDate, endDate, name, location, description);
    }

    private ExchangeDayRequestDTO(LocalDate startDate, LocalDate endDate, String name, String location, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.location = location;
        this.description = description;
    }
}
