package com.sopra.eaplanner.event;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    @Query("SELECT e FROM Event e JOIN FETCH e.exchangeDay d LEFT JOIN FETCH e.registeredUsers WHERE d.startDate >= :currentDate")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);

}
