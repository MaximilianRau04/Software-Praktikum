package com.sopra.eaplanner.forumthread.forumpost;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forumposts")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @GetMapping("")
    public Iterable<ForumPost> getAllForumPosts() {
        return forumPostService.getForumPosts();
    }

    @GetMapping("/{id}")
    public ForumPost getForumPostById(@PathVariable Long id) {
        return forumPostService.getForumPost(id);
    }

    @PostMapping("")
    public ForumPost createForumPost(@Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.createForumPost(requestBody);
    }

    @PutMapping("/{id}")
    public ForumPost updateForumPost(@PathVariable Long id, @Valid @RequestBody ForumPostDTO requestBody) {
        return forumPostService.updateForumPost(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        forumPostService.deleteForumPost(id);
    }
}
