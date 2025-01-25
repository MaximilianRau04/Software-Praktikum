package com.sopra.eaplanner.event.resources;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.resource.ResourceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventResourceRepository extends JpaRepository<EventResource, Long> {
    List<EventResource> findByEvent(Event event);

    Optional<EventResource> findFirstByEventAndResource(Event event, ResourceItem resource);
}

