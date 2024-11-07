package com.sopra.eaplanner.controller;

import com.sopra.eaplanner.model.Event;
import com.sopra.eaplanner.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService service;

    @GetMapping
    public List<Event> getEventsByExchangeDayId(Long exchangeDayId) {
        return service.getEventsByExchangeDayId(exchangeDayId);
    }

    @PostMapping
    public int addEvent(@RequestBody Event event) {
        return service.saveEvent(event);
    }
}
