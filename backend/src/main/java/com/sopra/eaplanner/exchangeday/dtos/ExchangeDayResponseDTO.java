package com.sopra.eaplanner.exchangeday.dtos;

import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.locations.LocationDTO;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for representing an ExchangeDay in the response.
 * This class is used to structure the data sent back to the client when an ExchangeDay is retrieved.
 *
 * <p>The fields in this DTO correspond to the properties of the ExchangeDay entity, but may not contain all the entity fields.</p>
 *
 * <p>The response will include:</p>
 * <ul>
 *   <li>{@code id} - The unique identifier of the ExchangeDay.</li>
 *   <li>{@code startDate} - The start date of the ExchangeDay.</li>
 *   <li>{@code endDate} - The end date of the ExchangeDay.</li>
 *   <li>{@code name} - The name of the ExchangeDay.</li>
 *   <li>{@code location} - The location of the ExchangeDay, represented as a {@link LocationDTO}.</li>
 *   <li>{@code description} - A brief description of the ExchangeDay.</li>
 * </ul>
 *
 * <p>Instances of this class are typically used for sending data to the frontend in response to ExchangeDay retrieval requests.</p>
 */
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
