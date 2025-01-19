package com.sopra.eaplanner.resource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.resourceAssignment.ResourceAssignment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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

    private Integer capacity;

    @NotNull
    private String location;

    private Boolean availability = true;

    @JsonBackReference
    @OneToMany(mappedBy = "resource")
    private List<ResourceAssignment> assignments;

    public ResourceItem(Long id, String name, ResourceType type, String description, Integer capacity, String location, Boolean availability) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.location = location;
        this.availability = availability;
    }

    public ResourceItem(ResourceRequest resourceRequest) {
        this.name = resourceRequest.getName();
        this.type = Enum.valueOf(ResourceType.class, resourceRequest.getType().toString());
        this.description = resourceRequest.getDescription();
        this.capacity = resourceRequest.getCapacity();
        this.location = resourceRequest.getLocation();
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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


