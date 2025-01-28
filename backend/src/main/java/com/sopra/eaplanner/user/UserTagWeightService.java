package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.participation.EventParticipation;
import com.sopra.eaplanner.event.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserTagWeightService {

    @Autowired
    private UserTagWeightRepository userTagWeightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public List<EventResponseDTO> recommendEvents(Long userId, Integer limit) {
        User userForRecommendations = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Tag> topTags = userTagWeightRepository.findTopTagsForUser(userForRecommendations)
                .stream()
                .limit(limit)
                .toList();

        if (topTags.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> recommendedEvents = eventRepository.findEventsByTags(topTags);

        Set<Event> userParticipatedEvents = userForRecommendations.getParticipations()
                .stream()
                .map(EventParticipation::getEvent)
                .collect(Collectors.toSet());

        List<Event> filteredRecommendedEvents = recommendedEvents.stream()
                .filter(event -> !userParticipatedEvents.contains(event))
                .toList();

        return filteredRecommendedEvents.stream()
                .map(EventResponseDTO::new)
                .toList();
    }
}
