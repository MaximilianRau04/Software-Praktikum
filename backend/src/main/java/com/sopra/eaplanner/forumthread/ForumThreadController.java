package com.sopra.eaplanner.forumthread;

import com.sopra.eaplanner.forumpost.ForumPost;
import com.sopra.eaplanner.forumpost.ForumPostRepository;
import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forumthreads")
@Validated
public class ForumThreadController {

    @Autowired
    private ForumThreadService forumThreadService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @GetMapping("")
    public ResponseEntity<Iterable<ForumThread>> getAllForumThreads() {
        Iterable<ForumThread> threads = forumThreadService.getAllForumThreads();
        return ResponseEntity.ok(threads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumThreadResponseDTO> getForumThreadById(@PathVariable Long id) {
        Optional<ForumThread> forumThread = forumThreadService.getForumThreadById(id);
        if (forumThread.isPresent()) {
            List<ForumPost> posts = forumPostRepository.findByForumThreadId(id);
            forumThread.get().setForumPosts(posts);
            return ResponseEntity.ok(new ForumThreadResponseDTO(forumThread.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<ForumThread> createForumThread(@Valid @RequestBody ForumThreadRequest request) {
        Optional<Event> event = eventRepository.findById(request.getEventId());
        if (event.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        ForumThread createdThread = forumThreadService.createForumThread(request, event.get());
        event.get().getForumThreads().add(createdThread);
        eventRepository.save(event.get());
        return ResponseEntity.ok(createdThread);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumThread> updateForumThread(
            @PathVariable Long id,
            @Valid @RequestBody ForumThreadRequest request) {
        Optional<ForumThread> updatedThread = forumThreadService.updateForumThread(id, request);
        return updatedThread.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForumThread(@PathVariable Long id) {
        boolean isDeleted = forumThreadService.deleteForumThread(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
