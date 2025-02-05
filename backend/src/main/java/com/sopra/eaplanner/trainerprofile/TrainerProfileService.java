package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.event.tags.Tag;
import com.sopra.eaplanner.event.tags.TagResponseDTO;
import com.sopra.eaplanner.event.tags.TagService;
import com.sopra.eaplanner.feedback.FeedbackRepository;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.FeedbackUtil;
import com.sopra.eaplanner.trainerprofile.comments.dtos.CommentDTO;
import com.sopra.eaplanner.trainerprofile.comments.dtos.EventWithCommentsDTO;
import com.sopra.eaplanner.trainerprofile.comments.dtos.TrainerCommentResponseDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainerProfileService {

    @Autowired
    private TrainerProfileRepository trainerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private FeedbackService feedbackService;

    public List<TrainerProfileResponseDTO> getAllTrainerProfiles() {
        Iterable<TrainerProfile> profiles = trainerProfileRepository.findAll();
        List<TrainerProfileResponseDTO> responseDTOs = new ArrayList<>();
        for (TrainerProfile profile : profiles) {
            responseDTOs.add(new TrainerProfileResponseDTO(profile));
        }
        return responseDTOs;
    }

    public TrainerProfileResponseDTO getTrainerProfileById(Long id) {
        TrainerProfile trainer = trainerProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user = trainer.getUser();
        if(user.getTrainerProfile() == null){
            throw new EntityNotFoundException("Trainer profile not set for this user");
        }
        return new TrainerProfileResponseDTO(user.getTrainerProfile());
    }

    public User getUserOfTrainerProfile(Long id) {
        Optional<TrainerProfile> profileOpt = trainerProfileRepository.findById(id);
        return profileOpt.map(TrainerProfile::getUser).orElse(null);
    }

    public TrainerCommentResponseDTO getReceivedComments(Long trainerId) {
        TrainerProfile trainerProfile = trainerProfileRepository.findById(trainerId).orElseThrow(() -> new EntityNotFoundException("Trainer Profile not found"));

        List<Event> events = eventRepository.findAllEventsOfOrganizer(trainerProfile.getUser().getId());

        List<EventWithCommentsDTO> preparedEvents = events.stream()
                // Mapping events to the required dTO
                .map((event) -> new EventWithCommentsDTO(event.getId(),
                        event.getName(),
                        event.getFeedbacks().stream()
                                // Mapping feedback to the CommentDTO and collecting a CommentDTO to be sent off
                                .map(feedback -> new CommentDTO(
                                        feedback.getId(),
                                        feedback.getEvent().getId(),
                                        feedback.getEnjoymentComment(),
                                        feedback.isAnonymousFeedback() ? "Anonym" : feedback.getUser().getFirstname() + " " + feedback.getUser().getLastname(),
                                        feedback.getEvent().getName(),
                                        FeedbackUtil.getFeedbackRating(feedback)
                                ))
                                .toList()))
                .toList();

        return new TrainerCommentResponseDTO(trainerId, preparedEvents);
    }

    public Set<TagResponseDTO> getExpertiseTags(Long id) {
        TrainerProfile profile = trainerProfileRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Trainer Profile not found"));

        return profile.getExpertiseTags().stream()
                .map(tag -> new TagResponseDTO(tag, true))
                .collect(Collectors.toSet());
    }

    public List<TagResponseDTO> setExpertiseTags(Long profileId, Set<String> tags){
        TrainerProfile profile = trainerProfileRepository.findById(profileId).orElseThrow(() -> new EntityNotFoundException("Trainer Profile not found"));
        Set<Tag> setTags = tagService.mergeAndGetTagsFromRequest(tags);
        profile.setExpertiseTags(setTags);
        trainerProfileRepository.save(profile);
        return setTags.stream().map(TagResponseDTO::new).collect(Collectors.toList());
    }

    public TrainerProfileResponseDTO updateTrainerProfile(Long id, TrainerProfileRequestDTO request) {
        TrainerProfile existingProfile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer Profile not found"));

        existingProfile.setBio(request.getBio());

        TrainerProfile updatedProfile = trainerProfileRepository.save(existingProfile);
        return new TrainerProfileResponseDTO(updatedProfile);
    }

    public boolean deleteTrainerProfile(Long id) {
        if (trainerProfileRepository.existsById(id)) {
            trainerProfileRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
