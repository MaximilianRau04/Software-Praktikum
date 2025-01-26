package com.sopra.eaplanner.exchangeday.dtos;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for the request to create or update an ExchangeDay.
 * This DTO is used to capture the input data for an ExchangeDay and includes validation annotations.
 *
 * <p>It ensures that the data for creating or updating an ExchangeDay is valid and correctly formatted.</p>
 *
 * <p>Fields in this class are validated as follows:</p>
 * <ul>
 *   <li>{@code startDate} and {@code endDate} must be a valid future or present date (using {@code @FutureOrPresent} annotation).</li>
 *   <li>{@code name} must be between 3 and 100 characters (using {@code @Size} annotation).</li>
 *   <li>{@code locationId} cannot be {@code null} (using {@code @NotNull} annotation).</li>
 *   <li>{@code description} cannot exceed 255 characters (using {@code @Size} annotation).</li>
 * </ul>
 *
 * <p>Instances of this class are typically used as input data for creating or updating ExchangeDay entities in the backend.</p>
 */
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
