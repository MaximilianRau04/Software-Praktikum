package com.sopra.eaplanner.trainerprofile.comments.dtos;

import java.util.List;

public record TrainerCommentResponseDTO(
        Long trainerId,
        List<EventWithCommentsDTO> events
) {}
