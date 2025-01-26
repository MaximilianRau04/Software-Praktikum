package com.sopra.eaplanner.trainerprofile.comments.dtos;

public record CommentDTO(
        Long id,
        Long eventId,
        String comment,
        String author,
        String eventName,
        Double rating
) {
}
