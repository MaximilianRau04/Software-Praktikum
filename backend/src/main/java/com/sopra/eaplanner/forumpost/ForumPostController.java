package com.sopra.eaplanner.forumpost;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForumPostController {

    @Autowired
    private ForumPostService forumPostService;

    @GetMapping("/forumposts")
    public Iterable<ForumPost> getAllForumPosts() {
        return forumPostService.getForumPosts();
    }

    @GetMapping("/forumposts/{id}")
    public ForumPost getForumPostById(@PathVariable Long id) {
        return forumPostService.getForumPost(id);
    }

    @PostMapping("/forumposts")
    public ForumPost createForumPost(@Valid @RequestBody ForumPost requestBody) {
        return forumPostService.createForumPost(requestBody);
    }

    // TODO: PutMapping here

    @DeleteMapping("/forumposts/{id}")
    public void deleteForumPost(@PathVariable Long id) {
        forumPostService.deleteForumPost(id);
    }
}
