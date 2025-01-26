package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.locations.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Represents an ExchangeDay entity in the system.
 * An ExchangeDay is a scheduled period that includes a name, start and end date,
 * a location, and an optional description. Each ExchangeDay may be associated with
 * one or more events.
 *
 * <p>This entity maps to the {@code exchange_days} table in the database.</p>
 *
 * <p>The main attributes of an ExchangeDay include:</p>
 * <ul>
 *   <li>{@code id} - The unique identifier of the ExchangeDay.</li>
 *   <li>{@code startDate} - The start date of the ExchangeDay, which must be in the present or future.</li>
 *   <li>{@code endDate} - The end date of the ExchangeDay, which must be in the present or future.</li>
 *   <li>{@code name} - The name of the ExchangeDay, which must be between 3 and 100 characters.</li>
 *   <li>{@code location} - The location of the ExchangeDay, referenced by a {@link Location} entity.</li>
 *   <li>{@code description} - A brief description of the ExchangeDay, which must not exceed 255 characters.</li>
 *   <li>{@code events} - A list of events associated with this ExchangeDay, mapped by the {@code exchangeDay} field in the {@link Event} entity.</li>
 * </ul>
 *
 * <p>Instance of this class are used to represent and persist ExchangeDay data.</p>
 */
@Entity
@Table(name = "exchange_days")
public class ExchangeDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @OneToMany(mappedBy = "exchangeDay", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Event> events = new ArrayList<>();

    public ExchangeDay() {
    }

    public ExchangeDay(Long id, LocalDate startDate, LocalDate endDate, String name, Location location, String description) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public ExchangeDay(ExchangeDayRequestDTO dto, Location location) {
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.name = dto.getName();
        this.location = location;
        this.description = dto.getDescription();
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}