package com.sopra.eaplanner.trainerprofile.comments;

import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.feedback.FeedbackRepository;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.FeedbackUtil;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import com.sopra.eaplanner.trainerprofile.TrainerProfileRepository;
import com.sopra.eaplanner.trainerprofile.comments.dtos.CommentDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
                pinnedComment.getFeedback().getEvent().getId(),
                pinnedComment.getFeedback().getEnjoymentComment(),

                pinnedComment.getFeedback().isAnonymousFeedback()? "Anonym": pinnedComment.getFeedback().getUser().getFirstname() + " " + pinnedComment.getFeedback().getUser().getLastname(),
                pinnedComment.getFeedback().getEvent().getName(),
                FeedbackUtil.getFeedbackRating(pinnedComment.getFeedback()))).toList();
    }

    @Transactional
    public String updatePinnedComments(Long trainerId, List<Long> newFeedbackIds) {
        TrainerProfile trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found."));

        if (newFeedbackIds.size() > 6) {
            throw new IllegalArgumentException("A trainer can only pin up to 6 comments.");
        }

        List<PinnedComment> currentPinnedComments = pinnedCommentRepository.findByTrainerId(trainerId);

        List<PinnedComment> commentsToUnpin = currentPinnedComments.stream()
                .filter(pinnedComment -> !newFeedbackIds.contains(pinnedComment.getFeedback().getId()))
                .toList();

        List<Long> currentPinnedFeedbackIds = currentPinnedComments.stream()
                .map(pinnedComment -> pinnedComment.getFeedback().getId())
                .toList();

        List<Long> feedbackIdsToPin = newFeedbackIds.stream()
                .filter(id -> !currentPinnedFeedbackIds.contains(id))
                .toList();

        pinnedCommentRepository.deleteAll(commentsToUnpin);

        List<Feedback> feedbackToPin = feedbackIdsToPin.stream()
                .map(id -> feedbackRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Feedback not found: ID " + id)))
                .toList();

        List<PinnedComment> newPinnedComments = feedbackToPin.stream()
                .map(feedback -> {
                    PinnedComment pinnedComment = new PinnedComment();
                    pinnedComment.setTrainer(trainer);
                    pinnedComment.setFeedback(feedback);
                    pinnedComment.setPinnedAt(LocalDateTime.now());
                    return pinnedComment;
                })
                .toList();

        pinnedCommentRepository.saveAll(newPinnedComments);

        return "Pinned comments have been updated.";
    }
}
