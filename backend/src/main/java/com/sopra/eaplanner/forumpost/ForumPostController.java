package com.sopra.eaplanner.forumpost;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forumposts")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @GetMapping("")
    public Iterable<ForumPostResponseDTO> getAllForumPosts() {
        return forumPostService.getForumPosts();
    }

    @GetMapping("/{id}")
    public ForumPostResponseDTO getForumPostById(@PathVariable Long id) {
        return forumPostService.getForumPost(id);
    }

    @PostMapping("")
    public ForumPostResponseDTO createForumPost(@Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.createForumPost(requestBody);
    }

    @PutMapping("/{id}")
    public ForumPostResponseDTO updateForumPost(@PathVariable Long id, @Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.updateForumPost(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        forumPostService.deleteForumPost(id);
    }
}
