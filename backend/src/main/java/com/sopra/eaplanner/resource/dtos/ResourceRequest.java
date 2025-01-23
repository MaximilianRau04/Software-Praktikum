package com.sopra.eaplanner.resource.dtos;

public class ResourceRequest {

    private String name;
    private String type;
    private String description;
    private Long locationId;
    private Boolean availability;
    private Integer capacity;

    public ResourceRequest(String name, String type, String description,
                            Long locationId, Boolean availability, Integer capacity) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.locationId = locationId;
        this.availability = availability;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
