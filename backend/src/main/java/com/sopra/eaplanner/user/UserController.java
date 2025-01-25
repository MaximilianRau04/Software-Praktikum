package com.sopra.eaplanner.user;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.participation.EventParticipationDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.forumpost.ForumPostResponseDTO;
import com.sopra.eaplanner.reward.dtos.RewardResponseDTO;
import com.sopra.eaplanner.trainerprofile.HostedEventsDTO;
import com.sopra.eaplanner.trainerprofile.TrainerProfileResponseDTO;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserTagWeightService userTagWeightService;

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

    @GetMapping("/{id}/recommendedEvents")
    public ResponseEntity<List<EventResponseDTO>> getRecommendedEvents(@PathVariable Long id, @RequestParam("limit") Integer limit) {
        List<EventResponseDTO> recommendedEvents = userTagWeightService.recommendEvents(id, limit);
        return ResponseEntity.ok().body(recommendedEvents);
    }

    @GetMapping("/{userId}/hostedEvents")
    public ResponseEntity<HostedEventsDTO> getHostedEvents(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getHostedEvents(userId));
    }

    @GetMapping("/{id}/feedback")
    public Iterable<FeedbackResponseDTO> getGivenFeedback(@PathVariable Long id) {
        return userService.getGivenFeedback(id);
    }

    @GetMapping("/{id}/forumPosts")
    public Iterable<ForumPostResponseDTO> getUserForumPosts(@PathVariable Long id) {
        return userService.getUserForumPosts(id);
    }

    @GetMapping("/{id}/trainerProfile")
    public TrainerProfileResponseDTO getTrainerProfile(@PathVariable Long id) {
        return userService.getTrainerProfile(id);
    }

    @GetMapping("/{id}/rewards")
    public Iterable<RewardResponseDTO> getUserRewards(@PathVariable Long id) {
        return userService.getUserRewards(id);
    }

    @GetMapping("/{id}/participations")
    public Iterable<EventParticipationDTO> getParticipations(@PathVariable Long id) {
        return userService.getParticipations(id);
    }

    @GetMapping("/{id}/pendingFeedbackEvents")
    public Iterable<EventResponseDTO> getPendingFeedbacks(@PathVariable Long id) {
        return userService.getPendingFeedbackEvents(id);
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