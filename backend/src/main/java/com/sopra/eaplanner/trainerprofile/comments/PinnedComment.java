package com.sopra.eaplanner.trainerprofile.comments;

import com.sopra.eaplanner.feedback.Feedback;
import com.sopra.eaplanner.trainerprofile.TrainerProfile;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PinnedComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private TrainerProfile trainer;

    @OneToOne
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    private LocalDateTime pinnedAt;

    public PinnedComment() {}

    public PinnedComment(TrainerProfile trainer, Feedback feedback) {
        this.trainer = trainer;
        this.feedback = feedback;
        this.pinnedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public TrainerProfile getTrainer() {
        return trainer;
    }
    public void setTrainer(TrainerProfile trainer) {
        this.trainer = trainer;
    }
    public Feedback getFeedback() {
        return feedback;
    }
    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }
    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }
}
