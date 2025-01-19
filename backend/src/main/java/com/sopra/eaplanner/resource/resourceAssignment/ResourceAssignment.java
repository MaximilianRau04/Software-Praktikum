package com.sopra.eaplanner.resource.resourceAssignment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.resource.ResourceItem;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class ResourceAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    @JsonBackReference
    private ResourceItem resource;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference
    private Event event;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    public ResourceAssignment(Long id, ResourceItem resource, Event event, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.resource = resource;
        this.event = event;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ResourceAssignment() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceItem getResource() {
        return resource;
    }

    public void setResource(ResourceItem resource) {
        this.resource = resource;
    }

    public Event getEvent() { return event; }

    public void setEvent(Event event) { this.event = event; }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime;
    }

}
