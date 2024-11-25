package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/events")
    public Iterable<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/events/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.getEventWithUserIds(id);
    }

    @PostMapping("/events")
    public Event createEvent(@Valid @RequestBody EventDTO requestBody) {
        return eventService.createEvent(requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
