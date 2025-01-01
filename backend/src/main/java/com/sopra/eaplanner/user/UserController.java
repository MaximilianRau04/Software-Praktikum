package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.participation.EventParticipationDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public UserResponseDTO getUserByUsername(@RequestParam("username") String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("")
    public Iterable<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/registeredEvents")
    public Iterable<EventResponseDTO> getRegisteredEvents(@PathVariable Long id) {
        return userService.getRegisteredEvents(id);
    }

    @GetMapping("/{id}/feedback")
    public Iterable<FeedbackResponseDTO> getGivenFeedback(@PathVariable Long id) {
        return userService.getGivenFeedback(id);
    }

    //TODO: @GetMapping("/{id}/forumPosts")
    //TODO: @GetMapping("/{id}/trainerProfile")
    //TODO: @GetMapping("/{id}/rewards")

    @GetMapping("/{id}/participations")
    public Iterable<EventParticipationDTO> getParticipations(@PathVariable Long id) {
        return userService.getParticipations(id);
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestBody) {
        UserResponseDTO savedDTO = userService.createUser(requestBody);
        URI location = URI.create("/api/users/" + savedDTO.getId());

        return ResponseEntity.created(location).body(savedDTO);
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO requestBody) {
        return userService.updateUser(id, requestBody);
    }

    @PostMapping("/{userId}/eventRegistration")
    public ResponseEntity<UserResponseDTO> registerUserToEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        User user = userService.registerUserToEvent(userId, eventId);
        URI location = URI.create("/api/users/" + user.getId());
        return ResponseEntity.created(location).body(new UserResponseDTO(user));
    }

    @DeleteMapping("/{userId}/eventRegistration")
    public ResponseEntity<?> removeUserFromEvent(@PathVariable Long userId, @RequestParam Long eventId) {
        userService.removeUserFromEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}