package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.user.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventParticipationService eventParticipationService;

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(UserDTO user) {

        List<Event> registeredEvents = user.getRegisteredEventIds()
                .stream()
                .map(eventId -> eventRepository.findById(eventId)
                        .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId)))
                .toList();

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        newUser.setRole(user.getRole());
        newUser.setRegisteredEvents(registeredEvents);

        return userRepository.save(newUser);
    }

    public User updateUser(Long id, UserDTO user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setRole(user.getRole());

        List<Event> existingEvents = existingUser.getRegisteredEvents();

        List<Event> newEvents = user.getRegisteredEventIds().stream()
                .map(eventId -> eventRepository.findById(eventId)
                        .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId)))
                .toList();

        for (Event event : newEvents) {
            if (!existingEvents.contains(event)) {
                existingEvents.add(event);
            }
        }

        existingUser.setRegisteredEvents(existingEvents);

        return userRepository.save(existingUser);
    }

    public User registerUserToEvent(Long userId, Long eventId) {
        User userToRegister = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));
        Event eventForRegistration = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + eventId + " not found"));

        userToRegister.getRegisteredEvents().add(eventForRegistration);
        userRepository.save(userToRegister);

        eventForRegistration.getRegisteredUsers().add(userToRegister);
        eventParticipationService.createAttendance(userToRegister, eventForRegistration);
        eventRepository.save(eventForRegistration);
        return userToRegister;
    }

    public User removeUserFromEvent(Long userId, Long eventId) {
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found"));
        Event eventForRemoval = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + eventId + " not found"));

        userToRemove.getRegisteredEvents().remove(eventForRemoval);
        userRepository.save(userToRemove);

        eventForRemoval.getRegisteredUsers().remove(userToRemove);
        eventParticipationService.deleteAttendance(userToRemove, eventForRemoval);
        eventRepository.save(eventForRemoval);

        return userToRemove;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}