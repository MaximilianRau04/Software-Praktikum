package com.sopra.eaplanner.resource;

import com.sopra.eaplanner.locations.Location;
import com.sopra.eaplanner.locations.LocationRepository;
import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private LocationRepository locationRepository;

    public Iterable<ResourceResponse> getAllResources() {
        Iterable<ResourceItem> resources = resourceRepository.findAll();
        List<ResourceResponse> dtos = new ArrayList<>();
        for (ResourceItem resource : resources) {
            dtos.add(new ResourceResponse(resource));
        }
        return dtos;
    }

    public ResourceResponse getResourceById(Long id) {
        ResourceItem resource = resourceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resource not found"));

        return new ResourceResponse(resource);
    }

    public ResourceResponse createResource(ResourceRequest requestBody) {
        Location location = locationRepository.findById(requestBody.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        ResourceItem resourceToSave = resourceRepository.save(new ResourceItem(requestBody, location));
        resourceToSave.setAvailability(resourceToSave.getCapacity() > 0);
        return new ResourceResponse(resourceToSave);
    }

    public ResourceResponse updateResource(Long id, ResourceRequest updatedResource) {
        Location location = locationRepository.findById(updatedResource.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        ResourceItem resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        resource.setName(updatedResource.getName());
        resource.setType(Enum.valueOf(ResourceType.class, updatedResource.getType().toUpperCase()));
        resource.setDescription(updatedResource.getDescription());
        resource.setCapacity(updatedResource.getCapacity());
        resource.setLocation(location);
        resource.setAvailability(updatedResource.getAvailability());
        resourceRepository.save(resource);
        return new ResourceResponse(resource);
    }

    public ResourceResponse deleteResource(Long id) {
        ResourceItem resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        resourceRepository.deleteById(id);
        return new ResourceResponse(resource);
    }

    public Iterable<ResourceResponse> getResourcesByType(String type) {
        return StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .filter(resource -> resource.getType().toString().equalsIgnoreCase(type))
                        .map(ResourceResponse::new)
                .collect(Collectors.toList());
    }

    public Iterable<ResourceResponse> getRoomsByLocation(Long locationId) {
        return StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .filter(resource -> resource.getLocation().getId().equals(locationId) && resource.getType().equals(ResourceType.ROOM))
                .map(ResourceResponse::new)
                .collect(Collectors.toList());
    }

}
