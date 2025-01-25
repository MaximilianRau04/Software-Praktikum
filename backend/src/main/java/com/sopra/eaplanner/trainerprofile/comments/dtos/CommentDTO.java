package com.sopra.eaplanner.trainerprofile.comments.dtos;

public record CommentDTO(
        Long feedbackId,
        String comment,
        String author,
        String eventName,
        Double rating
) {
}
