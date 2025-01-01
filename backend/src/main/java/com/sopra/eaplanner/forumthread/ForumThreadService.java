package com.sopra.eaplanner.forumthread;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForumThreadService {

    @Autowired
    private ForumThreadRepository forumThreadRepository;


    @Autowired
    private EventRepository eventRepository;

    public Iterable<ForumThread> getAllForumThreads() {
        return forumThreadRepository.findAll();
    }

    public Optional<ForumThread> getForumThreadById(Long id) {
        return forumThreadRepository.findById(id);
    }

    public ForumThread createForumThread(ForumThreadRequest request, Event event) {
        ForumThread forumThread = new ForumThread();
        forumThread.setTitle(request.getTitle());
        forumThread.setDescription(request.getDescription());
        forumThread.setEvent(event);
        return forumThreadRepository.save(forumThread);
    }

    public Optional<ForumThread> updateForumThread(Long id, ForumThreadRequest request) {
        Optional<ForumThread> existingThread = forumThreadRepository.findById(id);
        if (existingThread.isPresent()) {
            ForumThread threadToUpdate = existingThread.get();
            threadToUpdate.setTitle(request.getTitle());
            threadToUpdate.setDescription(request.getDescription());
            threadToUpdate.setEvent(eventRepository.findById(request.getEventId()).get());
            return Optional.of(forumThreadRepository.save(threadToUpdate));
        }
        return Optional.empty();
    }

    public boolean deleteForumThread(Long id) {
        if (forumThreadRepository.existsById(id)) {
            forumThreadRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
