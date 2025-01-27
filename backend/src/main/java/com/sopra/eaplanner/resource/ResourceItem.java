package com.sopra.eaplanner.resource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.locations.Location;
import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.resourceAssignment.ResourceAssignment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Entity
public class ResourceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private String description;

    private int capacity;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    private Boolean availability = true;

    @JsonBackReference
    @OneToMany(mappedBy = "resource")
    private List<ResourceAssignment> assignments;

    public ResourceItem(Long id, String name, ResourceType type, String description, int capacity, Location location, Boolean availability) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.location = location;
        this.availability = availability;
    }

    public ResourceItem(ResourceRequest resourceRequest, Location location) {
        this.name = resourceRequest.getName();
        this.type = Enum.valueOf(ResourceType.class, resourceRequest.getType().toString());
        this.description = resourceRequest.getDescription();
        this.capacity = resourceRequest.getCapacity();
        this.location = location;
        this.availability = resourceRequest.getAvailability();
    }

    public ResourceItem() {}

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public List<ResourceAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<ResourceAssignment> assignments) {
        this.assignments = assignments;
    }

}


