package com.sopra.eaplanner.trainerprofile;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.feedback.FeedbackRepository;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
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
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<TrainerProfileResponseDTO> getAllTrainerProfiles() {
        Iterable<TrainerProfile> profiles = trainerProfileRepository.findAll();
        List<TrainerProfileResponseDTO> responseDTOs = new ArrayList<>();
        for (TrainerProfile profile : profiles) {
            responseDTOs.add(new TrainerProfileResponseDTO(profile));
        }
        return responseDTOs;
    }

    public TrainerProfileResponseDTO getTrainerProfileById(Long id) {
        TrainerProfile profile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer Profile not found"));
        return new TrainerProfileResponseDTO(profile);
    }

    public User getUserOfTrainerProfile(Long id) {
        Optional<TrainerProfile> profileOpt = trainerProfileRepository.findById(id);
        return profileOpt.map(TrainerProfile::getUser).orElse(null);
    }

    public Iterable<FeedbackResponseDTO> getFeedback(Long id) {
        TrainerProfile trainerProfile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return trainerProfile.getFeedbacks()
                .stream()
                .map(FeedbackResponseDTO::new)
                .collect(Collectors.toSet());
    }

    public List<Map<String, String>> getPinnedComments(Long id) {
        Optional<TrainerProfile> trainerProfileOpt = trainerProfileRepository.findById(id);
        if (trainerProfileOpt.isEmpty()) {
            throw new RuntimeException("Trainer Profile not found for the specified trainer");
        }

        TrainerProfile trainerProfile = trainerProfileOpt.get();

        List<Map<String, String>> pinnedComments = new ArrayList<>();
        for (Map.Entry<String, String> entry : trainerProfile.getPinnedComments().entrySet()) {
            String commentType = entry.getKey();
            String commentContent = entry.getValue();

            Feedback feedback = feedbackRepository.findByTrainerProfileId(id)
                    .orElseThrow(() -> new RuntimeException("Feedback not found for pinned comment"));

            Map<String, String> commentData = new HashMap<>();
            commentData.put("category", commentType);
            commentData.put("content", commentContent);
            commentData.put("feedbackId", feedback.getId().toString());

            pinnedComments.add(commentData);
        }

        return pinnedComments;
    }

    public Feedback pinComment(Long trainerId, Long feedbackId, String commentType) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findByTrainerProfileId(trainerId);
        if (feedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found for the specified trainer");
        }

        Feedback feedback = feedbackOpt.get();

        Optional<TrainerProfile> trainerProfileOpt = trainerProfileRepository.findById(trainerId);
        if (trainerProfileOpt.isEmpty()) {
            throw new RuntimeException("Trainer Profile not found for the specified trainer");
        }

        TrainerProfile trainerProfile = trainerProfileOpt.get();

        if (trainerProfile.getPinnedComments().size() > 5) {
            throw new RuntimeException("Pinned comment limit exceeded");
        }

        switch (commentType.toLowerCase()) {
            case "enjoyment":
                feedback.setEnjoymentCommentPinned(true);
                trainerProfile.getPinnedComments().put("enjoyment", feedback.getEnjoymentComment());
                break;
            case "improvement":
                feedback.setImprovementCommentPinned(true);
                trainerProfile.getPinnedComments().put("improvement", feedback.getImprovementComment());
                break;
            case "request":
                feedback.setRequestCommentPinned(true);
                trainerProfile.getPinnedComments().put("request", feedback.getRequestComment());
                break;
            case "personalimprovement":
                feedback.setPersonalImprovementCommentPinned(true);
                trainerProfile.getPinnedComments().put("personalimprovement", feedback.getPersonalImprovementComment());
                break;
            case "recommendation":
                feedback.setRecommendationCommentPinned(true);
                trainerProfile.getPinnedComments().put("recommendation", feedback.getRecommendationComment());
                break;
            default:
                throw new IllegalArgumentException("Invalid comment type");
        }

        trainerProfileRepository.save(trainerProfile);

        return feedback;
    }

    public Feedback unpinComment(Long trainerId, Long feedbackId, String commentType) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findByTrainerProfileId(trainerId);
        if (feedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found for the specified trainer");
        }

        Feedback feedback = feedbackOpt.get();

        Optional<TrainerProfile> trainerProfileOpt = trainerProfileRepository.findById(trainerId);
        if (trainerProfileOpt.isEmpty()) {
            throw new RuntimeException("Trainer Profile not found for the specified trainer");
        }

        TrainerProfile trainerProfile = trainerProfileOpt.get();

        switch (commentType.toLowerCase()) {
            case "enjoyment":
                feedback.setEnjoymentCommentPinned(false);
                trainerProfile.getPinnedComments().remove("enjoyment");
                break;
            case "improvement":
                feedback.setImprovementCommentPinned(false);
                trainerProfile.getPinnedComments().remove("improvement");
                break;
            case "request":
                feedback.setRequestCommentPinned(false);
                trainerProfile.getPinnedComments().remove("request");
                break;
            case "personalimprovement":
                feedback.setPersonalImprovementCommentPinned(false);
                trainerProfile.getPinnedComments().remove("personalimprovement");
                break;
            case "recommendation":
                feedback.setRecommendationCommentPinned(false);
                trainerProfile.getPinnedComments().remove("recommendation");
                break;
            default:
                throw new IllegalArgumentException("Invalid comment type");
        }

        trainerProfileRepository.save(trainerProfile);

        return feedback;
    }

    public TrainerProfileResponseDTO createTrainerProfile(TrainerProfileRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TrainerProfile trainerProfile = new TrainerProfile();
        trainerProfile.setBio(request.getBio());
        trainerProfile.setAverageRating(request.getAverageRating());
        trainerProfile.setExpertiseTags(request.getExpertiseTags());
        trainerProfile.setUser(user);

        TrainerProfile savedProfile = trainerProfileRepository.save(trainerProfile);
        return new TrainerProfileResponseDTO(savedProfile);
    }



    public TrainerProfileResponseDTO updateTrainerProfile(Long id, TrainerProfileRequestDTO request) {
        TrainerProfile existingProfile = trainerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer Profile not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingProfile.setBio(request.getBio());
        existingProfile.setAverageRating(request.getAverageRating());
        existingProfile.setExpertiseTags(request.getExpertiseTags());
        existingProfile.setUser(user);

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
