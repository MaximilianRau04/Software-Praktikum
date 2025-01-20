package com.sopra.eaplanner.resource;

import com.sopra.eaplanner.resource.dtos.ResourceRequest;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

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
        ResourceItem resourceToSave = resourceRepository.save(new ResourceItem(requestBody));
        return new ResourceResponse(resourceToSave);
    }

    public ResourceResponse updateResource(Long id, ResourceItem updatedResource) {
        ResourceItem resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Resource not found"));
        resource.setName(updatedResource.getName());
        resource.setType(updatedResource.getType());
        resource.setDescription(updatedResource.getDescription());
        resource.setCapacity(updatedResource.getCapacity());
        resource.setLocation(updatedResource.getLocation());
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
}
