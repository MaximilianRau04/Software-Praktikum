package com.sopra.eaplanner.feedback.dtos;

public class CommentDTO {
    private String comment;
    private boolean pinned;

    private Long feedbackId;

    public CommentDTO() {}

    public CommentDTO(String comment, boolean pinned, Long feedbackId) {
        this.comment = comment;
        this.pinned = pinned;
        this.feedbackId = feedbackId;
    }

    public CommentDTO(String comment, Long feedbackId) {
        this.comment = comment;
        this.feedbackId = feedbackId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
}
