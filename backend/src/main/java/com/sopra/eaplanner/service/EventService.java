package com.sopra.eaplanner.service;

import com.sopra.eaplanner.model.Event;
import com.sopra.eaplanner.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repository;

    public int saveEvent(Event event) {
        return repository.save(event);
    }

    public List<Event> getEventsByExchangeDayId(Long exchangeDayId) {
        return repository.findByExchangeDayId(exchangeDayId);
    }
}