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

    private int quantity;

    public ResourceAssignment(Long id, ResourceItem resource, int quantity, Event event) {
        this.id = id;
        this.resource = resource;
        this.quantity = quantity;
        this.event = event;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
