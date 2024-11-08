package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.Event;
import com.sopra.eaplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    public Event createEvent(Event event) {
        return repository.save(event);
    }

    public Iterable<Event> getAllEvents() {
        return repository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return repository.findById(id);
    }

    public void deleteEvent(Long id) {
        repository.deleteById(id);
    }

}