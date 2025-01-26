package com.sopra.eaplanner.trainerprofile.comments;

import com.sopra.eaplanner.trainerprofile.comments.dtos.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pinned-comments")
public class PinnedCommentController {

    @Autowired
    private PinnedCommentService pinnedCommentService;

    @GetMapping("/{trainerId}")
    public List<CommentDTO> getPinnedComments(@PathVariable Long trainerId) {
        return pinnedCommentService.getPinnedComments(trainerId);
    }
}
