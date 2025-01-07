package com.sopra.eaplanner.notification;

import com.sopra.eaplanner.event.notification.EventReminderHandler;
import com.sopra.eaplanner.forumthread.notification.ForumNotificationHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code NotificationHandler} interface defines a contract for processing different types of notifications.
 * <p>
 * This interface serves as a common base for handling various notification types (e.g., event reminders, forum posts, user mentions)
 * by implementing specific behavior for each notification type through the {@link #process(Notification)} method.
 * The goal is to keep the notification processing logic modular and type-specific, enabling future extensibility.
 * <p>
 * The {@link NotificationHandler} interface is meant to be implemented by classes that define behavior for handling specific types of notifications.
 * Each type of notification (such as {@code EventReminderNotification}) will have its own handler that implements the required logic
 * for processing that notification type.
 *
 * <p><b>Usage:</b></p>
 * <p>When a new notification is created, it will be associated with a specific handler based on its type (such as EventReminder).
 * The handler will then be responsible for processing the notification (e.g., logging, modifying data, sending additional information).
 * Each handler implements the process method, allowing you to handle different notification types in their own specialized way.</p>
 *
 * <p>Additionally, the {@code getContext(Notification)} method provides an opportunity for the handler to return type-specific data
 * related to the notification, which can then be used to enhance the frontend display or further processing.</p>
 * <p>
 * Example of usage:
 * <pre>
 *     NotificationHandler handler = notificationHandlerFactory.getNotificationHandler(notification.getType());
 *     handler.process(notification); // Invokes type-specific processing logic
 * </pre>
 *
 * @see Notification
 * @see EventReminderHandler
 * @see ForumNotificationHandler
 */
public interface NotificationHandler {

    /**
     * Processes the given notification based on its specific type.
     * <p>This method is meant to be implemented by each concrete handler to perform type-specific actions on the notification.
     * Actions could include logging, modifying the notification's content, preparing it for external systems, etc.</p>
     *
     * @param notification The notification object to process. This can be an event reminder, forum post, or any other type of notification.
     */
    void process(Notification notification);

    /**
     * Saves the given notification to the database while guaranteeing that type specific fields are saved according to
     * their discriminator column.
     *
     * @param notification The notification object to save to the database. This will have different dynamic types.
     */
    void saveToDatabase(Notification notification);

    /**
     * Provides additional context for the given notification, which can be used to enrich the data sent to the frontend or for
     * other processing purposes.
     * <p>The default implementation returns an empty map, but individual handlers can override this method to provide
     * context-specific data. For example, an {@code EventReminderHandler} might include the event's date and time in the context.</p>
     *
     * @param notification The notification for which context is needed.
     * @return A map containing key-value pairs of context data that are specific to the notification type.
     */
    default Map<String, Object> getContext(Notification notification) {
        return new HashMap<>();
    }
}
