package com.sopra.eaplanner.forumthread.notification;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.forumpost.ForumPost;
import com.sopra.eaplanner.forumthread.ForumThread;
import com.sopra.eaplanner.forumthread.ForumThreadRepository;
import com.sopra.eaplanner.notification.NotificationService;
import com.sopra.eaplanner.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ForumResponseService {
    private final EventRepository eventRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final NotificationService notificationService;

    @Autowired
    public ForumResponseService(EventRepository eventRepository, ForumThreadRepository forumThreadRepository, NotificationService notificationService){
        this.eventRepository = eventRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.notificationService = notificationService;
    }

    public void sendResponseNotification(Long eventId, Long threadId, User responder){
        Event eventWithForumThreads = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        ForumThread forumForNotifications = forumThreadRepository.findById(threadId).orElseThrow(() -> new EntityNotFoundException("Thread not found."));

        forumForNotifications.getForumPosts().stream()
                .map(ForumPost::getAuthor)
                .filter(author -> !Objects.equals(author.getId(), responder.getId()))
                .forEach(user -> sendReminder(eventWithForumThreads, forumForNotifications, user, responder.getFirstname() + " " + responder.getLastname()));
    }

    private void sendReminder(Event event, ForumThread thread, User user, String responderName){
        String title = event.getName();

        notificationService.createAndSendForumResponseNotification(title, user.getId(), event.getId(), thread.getId(), responderName);
    }
}
