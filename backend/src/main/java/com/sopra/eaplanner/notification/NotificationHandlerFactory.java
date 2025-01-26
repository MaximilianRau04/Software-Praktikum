package com.sopra.eaplanner.notification;

import com.sopra.eaplanner.event.notification.EventReminderHandler;
import com.sopra.eaplanner.forumthread.notification.ForumNotificationHandler;
import com.sopra.eaplanner.reward.notification.RewardNotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code NotificationHandlerFactory} is responsible for managing and providing the correct {@link NotificationHandler}
 * based on the notification type.
 * <p>
 * This factory pattern allows the application to easily retrieve a handler that is specific to a notification type, such as
 * {@code EventReminderHandler} for event reminders. It helps keep the logic for processing notifications modular and
 * extensible, as new types of notifications can be added with minimal changes to the rest of the system.
 * <p>
 * The factory maintains a map of notification types to their respective handlers and provides a method to retrieve the handler
 * based on the notification's type.
 *
 * <p><b>Usage:</b></p>
 * <pre>
 *     NotificationHandler handler = notificationHandlerFactory.getNotificationHandler(notification.getType());
 *     handler.process(notification);
 * </pre>
 */
@Component
public class NotificationHandlerFactory {
    private final Map<NotificationType, NotificationHandler> handlers = new HashMap<>();

    /**
     * Constructor that initializes the factory with available notification handlers.
     * <p>
     * The handlers are mapped by their respective {@link NotificationType}, allowing the factory to return the correct
     * handler based on the notification type.
     *
     * @param eventReminderHandler The handler responsible for processing {@code EventReminderNotification} types.
     */
    @Autowired
    public NotificationHandlerFactory(EventReminderHandler eventReminderHandler, ForumNotificationHandler forumNotificationHandler, RewardNotificationHandler rewardNotificationHandler) {
        handlers.put(NotificationType.EVENT_REMINDER, eventReminderHandler);
        handlers.put(NotificationType.FORUM_POST,forumNotificationHandler);
        handlers.put(NotificationType.REWARD, rewardNotificationHandler);
    }

    /**
     * Retrieves the appropriate {@link NotificationHandler} for the given notification type.
     *
     * @param type The type of notification.
     * @return The corresponding {@link NotificationHandler} for the provided notification type, or {@code null} if no handler
     * is found.
     */
    public NotificationHandler getNotificationHandler(NotificationType type) {
        return handlers.get(type);
    }
}