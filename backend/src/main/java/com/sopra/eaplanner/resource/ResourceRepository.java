package com.sopra.eaplanner.resource;

import com.sopra.eaplanner.locations.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends CrudRepository<ResourceItem, Long> {
    List<ResourceItem> findByTypeAndLocationAndAvailability(ResourceType type, Location location, Boolean availability);
}
