package com.sopra.eaplanner.exchangeday;

import com.sopra.eaplanner.event.Event;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "exchange_days")
public class ExchangeDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Future(message = "Date must be in the future")
    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    private String location;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @OneToMany(mappedBy = "exchangeDay", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Event> events = new ArrayList<>();

    public ExchangeDay() {}

    public ExchangeDay(Long id, LocalDate date, String name, String location, String description) {
        this.id = id;
        this.date = date;
        this.name = name;
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

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }
    public void removeEvent(Event event) {
        events.remove(event);
    }
}