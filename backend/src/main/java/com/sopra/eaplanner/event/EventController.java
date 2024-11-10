package com.sopra.eaplanner.event;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
        return eventService.getEventWithCostumerIds(id);
    }

    @PostMapping("/events")
    public Event createEvent(@Valid @RequestBody Event requestBody) {
        return eventService.createEvent(requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
