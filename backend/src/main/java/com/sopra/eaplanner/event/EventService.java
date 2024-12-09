package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventDTO;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayRepository;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeDayRepository exchangeDayRepository;

    public Event createEvent(EventDTO event) {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(event.getExchangeDayId())
                .orElseThrow(() -> new RuntimeException("ExchangeDay not found."));

        User eventOrganizer = userRepository.findById(event.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found."));

        List<User> registeredUsers = event.getRegisteredUserIds()
                .stream()
                .map((id)-> userRepository.findById(id).orElseThrow(()-> new RuntimeException("Registered User "+ id +" not found.")))
                .toList();

        Event eventToSave = new Event(event, exchangeDay, eventOrganizer, registeredUsers);

        return eventRepository.save(eventToSave);
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

    public EventDTO getEventWithUserIds(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found."));

        List<Long> registeredUserIds = event.getRegisteredUsers().stream()
                .map(User::getId)
                .toList();

        return new EventDTO(eventId, event, registeredUserIds);
    }
}