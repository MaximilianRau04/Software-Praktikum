package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainerProfiles")
public class TrainerProfileController {

    @Autowired
    private TrainerProfileService trainerProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("")
    public ResponseEntity<List<TrainerProfileResponseDTO>> getAllTrainerProfiles() {
        List<TrainerProfileResponseDTO> profiles = trainerProfileService.getAllTrainerProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerProfileResponseDTO> getTrainerProfileById(@PathVariable Long id) {
        TrainerProfileResponseDTO profile = trainerProfileService.getTrainerProfileById(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}/user")
    public ResponseEntity<User> getUserOfTrainerProfile(@PathVariable Long id) {
        User user = trainerProfileService.getUserOfTrainerProfile(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/feedback")
    public Iterable<FeedbackResponseDTO> getGivenFeedback(@PathVariable Long id) {
        return trainerProfileService.getFeedback(id);
    }

    @GetMapping("/{id}/pinned-comments")
    public List<Map<String, String>> getPinnedComments(@PathVariable Long id) {
        return trainerProfileService.getPinnedComments(id);
    }

    @PostMapping("/{trainerId}/{feedbackId}/pin")
    public Feedback pinComment(@PathVariable Long trainerId, @PathVariable Long feedbackId, @RequestParam String commentType) {
        return trainerProfileService.pinComment(trainerId, feedbackId, commentType);
    }

    @PostMapping("{trainerId}/{feedbackId}/unpin")
    public Feedback unpinComment(@PathVariable Long trainerId, @PathVariable Long feedbackId, @RequestParam String commentType) {
        return trainerProfileService.unpinComment(trainerId, feedbackId, commentType);
    }

    // get hosted events

    @PostMapping("")
    public ResponseEntity<TrainerProfileResponseDTO> createTrainerProfile(@Valid @RequestBody TrainerProfileRequestDTO request) {
        TrainerProfileResponseDTO savedDTO = trainerProfileService.createTrainerProfile(request);
        URI location = URI.create("/api/trainerProfiles/" + savedDTO.getId());
        return ResponseEntity.created(location).body(savedDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerProfileResponseDTO> updateTrainerProfile(
            @PathVariable Long id,
            @Valid @RequestBody TrainerProfileRequestDTO request) {
        TrainerProfileResponseDTO updatedDTO = trainerProfileService.updateTrainerProfile(id, request);
        return ResponseEntity.ok(updatedDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainerProfile(@PathVariable Long id) {
        boolean deleted = trainerProfileService.deleteTrainerProfile(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
