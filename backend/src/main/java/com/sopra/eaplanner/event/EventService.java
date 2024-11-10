package com.sopra.eaplanner.event;

import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayDTO;
import com.sopra.eaplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventDTO getEventWithCostumerIds(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event nicht gefunden"));

        List<Long> eventIds = event.getRegisteredUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        return new EventDTO(
                eventId,
                event.getStartTime(),
                event.getEndTime(),
                event.getName(),
                event.getRoom(),
                event.getDescription(),
                event.getExchangeDay(),
                event.getOrganizer(),
                eventIds
        );
    }
}