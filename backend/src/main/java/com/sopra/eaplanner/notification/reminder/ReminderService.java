package com.sopra.eaplanner.notification.reminder;

import com.sopra.eaplanner.event.Event;
import com.sopra.eaplanner.event.EventRepository;
import com.sopra.eaplanner.notification.NotificationService;
import com.sopra.eaplanner.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for sending event reminders to registered users.
 * This service runs on a scheduled task to check upcoming events and sends reminders
 * based on the configured reminder types (1 week, 1 day, 1 hour before the event).
 */
@Service
public class ReminderService {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    /**
     * Constructs the ReminderService with the necessary dependencies.
     *
     * @param eventRepository     The repository to fetch events from.
     * @param notificationService The service responsible for sending notifications.
     */
    @Autowired
    public ReminderService(EventRepository eventRepository, NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    /**
     * A scheduled task that runs at fixed intervals (every 10 minutes) to check upcoming events
     * and send reminders to users.
     * It fetches events happening in the future and sends the appropriate reminders (1 week, 1 day, 1 hour before the event).
     */
    @Scheduled(fixedRate = 6000)
    public void sendReminders() {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Event> eventsInFuture = eventRepository.findUpcomingEvents(dateTime.toLocalDate());
        System.out.println(eventsInFuture.size());
        for (Event event : eventsInFuture) {
            LocalDateTime eventTime = event.getStartDateTime();
            for (ReminderType type : ReminderType.values()) {
                if (shouldSendReminder(dateTime, eventTime, type, event.getRemindersSent())) {
                    sendReminder(event, type);
                }
            }
        }
    }

    /**
     * Determines if a reminder should be sent based on the current time, event time, reminder type,
     * and whether the reminder has already been sent.
     *
     * @param now           The current time.
     * @param eventTime     The time of the event.
     * @param reminderType  The type of reminder to check (e.g., 1 week, 1 day, 1 hour).
     * @param remindersSent The map of reminders already sent for this event.
     * @return true if the reminder should be sent, false otherwise.
     */
    private boolean shouldSendReminder(LocalDateTime now, LocalDateTime eventTime,
                                       ReminderType reminderType,
                                       Map<ReminderType, Boolean> remindersSent) {
        return switch (reminderType) {
            case ONE_WEEK_BEFORE ->
                    !remindersSent.getOrDefault(reminderType, false) && now.isAfter(eventTime.minusWeeks(1));
            case ONE_DAY_BEFORE ->
                    !remindersSent.getOrDefault(reminderType, false) && now.isAfter(eventTime.minusDays(1));
            case ONE_HOUR_BEFORE ->
                    !remindersSent.getOrDefault(reminderType, false) && now.isAfter(eventTime.minusHours(1));
        };
    }

    /**
     * Sends the appropriate reminder notification to users registered for the event.
     * This method constructs the notification and passes it to the NotificationService to send to each user.
     * It also guarantees that the type of reminder that has been sent will be flagged, so that users wont get spammed
     * with the same notification.
     *
     * @param event        The event for which the reminder is being sent.
     * @param reminderType The type of reminder (1 week, 1 day, 1 hour).
     */
    private void sendReminder(Event event, ReminderType reminderType) {
        String title = event.getName();

        List<Long> userIds = event.getRegisteredUsers().stream().map(User::getId).toList();

        for (Long userId : userIds) {
            notificationService.createAndSendEventReminder(title, event.getStartDateTime(), userId, event.getId());
        }

        event.getRemindersSent().put(reminderType, true);
        eventRepository.save(event);
    }
}
