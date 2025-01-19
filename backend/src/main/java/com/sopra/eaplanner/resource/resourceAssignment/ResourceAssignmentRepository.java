package com.sopra.eaplanner.resource.resourceAssignment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ResourceAssignmentRepository extends CrudRepository<ResourceAssignment, Long> {
    @Query("SELECT ra FROM ResourceAssignment ra WHERE ra.resource.id = :resourceId AND " +
            "ra.event.id = :eventId AND " +
            "((:startTime BETWEEN ra.startTime AND ra.endTime) OR " +
            "(:endTime BETWEEN ra.startTime AND ra.endTime) OR " +
            "(ra.startTime BETWEEN :startTime AND :endTime))")
    List<ResourceAssignment> findConflictingAssignments(@Param("resourceId") Long resourceId,
                                                        @Param("eventId") Long eventId,
                                                        @Param("startTime") LocalTime startTime,
                                                        @Param("endTime") LocalTime endTime);
}
