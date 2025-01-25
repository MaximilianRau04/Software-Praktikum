package com.sopra.eaplanner.trainerprofile.comments.dtos;

import java.util.List;

public record EventWithCommentsDTO(
        Long id,
        String name,
        List<CommentDTO> comments
) {
}
