package com.sopra.eaplanner.resource.resourceAssignment;

import com.sopra.eaplanner.event.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResourceAssignmentRepository extends CrudRepository<ResourceAssignment, Long> {
    List<ResourceAssignment> findByEvent(Event event);
}