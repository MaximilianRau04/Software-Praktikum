package com.sopra.eaplanner.forumthread;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.dtos.EventDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForumThreadController {

    @Autowired
    private ForumThreadService forumThreadService;

    @GetMapping("/forumthreads")
    public Iterable<Event> getAllForumThreads() {
        return null;
    }

    @GetMapping("/forumthreads/{id}")
    public EventDTO getForumThreadById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/forumthreads")
    public Event createForumThread(@Valid @RequestBody Event requestBody) {
        return null;
    }

    // TODO: PutMapping here

    @DeleteMapping("/forumthreads/{id}")
    public void deleteForumThread(@PathVariable Long id) {
        return;
    }
}
