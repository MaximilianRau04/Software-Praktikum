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
    public Iterable<ForumPost> getAllForumPosts() {
        return forumPostService.getForumPosts();
    }

    @GetMapping("/{id}")
    public ForumPost getForumPostById(@PathVariable Long id) {
        return forumPostService.getForumPost(id);
    }

    @PostMapping("")
    public ForumPost createForumPost(@Valid @RequestBody ForumPost requestBody) {
        return forumPostService.createForumPost(requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        forumPostService.deleteForumPost(id);
    }
}
