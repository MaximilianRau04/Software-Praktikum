package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.tags.TagResponseDTO;
import com.sopra.eaplanner.trainerprofile.comments.PinnedCommentService;
import com.sopra.eaplanner.trainerprofile.comments.dtos.CommentDTO;
import com.sopra.eaplanner.trainerprofile.comments.dtos.TrainerCommentResponseDTO;
import com.sopra.eaplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/trainerProfiles")
public class TrainerProfileController {

    @Autowired
    private TrainerProfileService trainerProfileService;

    @Autowired
    private PinnedCommentService pinnedCommentService;

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

    @GetMapping("/{id}/pinned-comments")
    public ResponseEntity<List<CommentDTO>> getPinnedComments(@PathVariable Long id) {
        return ResponseEntity.ok(pinnedCommentService.getPinnedComments(id));
    }

    @GetMapping("/{id}/expertiseTags")
    public ResponseEntity<Set<TagResponseDTO>> getExpertiseTags(@PathVariable Long id) {
        return ResponseEntity.ok().body(trainerProfileService.getExpertiseTags(id));
    }

    @GetMapping("/{trainerId}/comments-by-event")
    public ResponseEntity<TrainerCommentResponseDTO> getReceivedComments(@PathVariable Long trainerId) {
        return ResponseEntity.ok().body(trainerProfileService.getReceivedComments(trainerId));
    }

    @PostMapping("/{trainerId}/pinned-comments")
    public ResponseEntity<?> pinComments(
            @PathVariable Long trainerId,
            @RequestBody ArrayList<Long> feedbackIds) {
        return ResponseEntity.ok(pinnedCommentService.pinComments(trainerId, feedbackIds));
    }

    @PostMapping("")
    public ResponseEntity<TrainerProfileResponseDTO> createTrainerProfile(@Valid @RequestBody TrainerProfileRequestDTO request) {
        TrainerProfileResponseDTO savedDTO = trainerProfileService.createTrainerProfile(request);
        URI location = URI.create("/api/trainerProfiles/" + savedDTO.getId());
        return ResponseEntity.created(location).body(savedDTO);
    }

    @PostMapping("/{profileId}/expertiseTags")
    public ResponseEntity<List<TagResponseDTO>> postTagsToTrainerProfile(@PathVariable Long profileId, @RequestBody HashSet<String> tags){
        return ResponseEntity.ok(trainerProfileService.setExpertiseTags(profileId, tags));
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
