package com.sopra.eaplanner.trainerprofile.comments;

import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.feedback.FeedbackRepository;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.trainerprofile.comments.dtos.CommentDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PinnedCommentService {
    @Autowired
    private PinnedCommentRepository pinnedCommentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private TrainerProfileRepository trainerRepository;

    @Autowired
    private FeedbackService feedbackService;

    public List<CommentDTO> getPinnedComments(Long trainerId) {
        List<PinnedComment> comments = pinnedCommentRepository.findByTrainerId(trainerId);
        return comments.stream().map(pinnedComment -> new CommentDTO(
                pinnedComment.getFeedback().getId(),
                pinnedComment.getFeedback().getEnjoymentComment(),
                pinnedComment.getFeedback().getUser().getFirstname() + " " + pinnedComment.getFeedback().getUser().getLastname(),
                pinnedComment.getFeedback().getEvent().getName(),
                feedbackService.getFeedbackRating(pinnedComment.getFeedback()))).toList();
    }

    public String pinComments(Long trainerId, List<Long> feedbackIds) {

        if (feedbackIds.isEmpty()) {
            return "";
        }

        if (pinnedCommentRepository.countByTrainerId(trainerId) >= 6) {
            throw new IllegalArgumentException("A trainer can only pin up to 6 comments.");
        }

        List<Feedback> commentsFromFeedback = feedbackIds.stream()
                .map(id -> feedbackRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Feedback not found.")))
                .toList();

        List<PinnedComment> pinnedComments = commentsFromFeedback.stream()
                .map(f -> new PinnedComment(
                        trainerRepository.findById(trainerId)
                                .orElseThrow(() -> new EntityNotFoundException("Trainer not found.")),
                        f))
                .toList();

        pinnedCommentRepository.saveAll(pinnedComments);

        return "Saved PinnedComments";
    }

    public void unpinComment(Long pinnedCommentId) {
        pinnedCommentRepository.deleteById(pinnedCommentId);
    }
}
