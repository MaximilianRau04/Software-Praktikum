package com.sopra.eaplanner.sse;

import com.sopra.eaplanner.notification.Notification;
import com.sopra.eaplanner.notification.NotificationHandlerFactory;
import com.sopra.eaplanner.notification.NotificationResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller responsible for handling Server-Sent Events (SSE) notifications for users.
 * <p>
 * This controller provides a mechanism to stream real-time notifications to a client using SSE,
 * where the client can subscribe to receive notifications for a specific user identified by their ID.
 * The notifications are sent through an {@link SseEmitter}, which keeps the connection open for the client to receive events.
 * </p>
 * <p>
 * The controller also supports sending notifications to a specific user through the {@link #sendNotification(Long, Notification)} method.
 * </p>
 */
@RestController
@RequestMapping("/sse")
public class NotificationSseController {

    @Autowired
    private NotificationHandlerFactory notificationHandlerFactory;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Streams notifications for a specific user identified by their user ID.
     * <p>
     * This method creates a new {@link SseEmitter} for the given user ID, which keeps the connection open
     * so that notifications can be sent to the client in real time.
     * The emitter is stored in a map, and it is removed when the connection is completed, timed out, or encounters an error.
     * </p>
     *
     * @param userId the ID of the user for whom the notifications are to be streamed.
     * @return an {@link SseEmitter} that the client can use to receive notifications.
     */
    @GetMapping("/notifications/{userId}")
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

    /**
     * Sends a notification to the specified user via SSE.
     * <p>
     * This method retrieves the {@link SseEmitter} associated with the given user ID and sends the notification.
     * If the emitter is not available or the connection encounters an error, the emitter is removed from the map.
     * </p>
     *
     * @param userId the ID of the user to send the notification to.
     * @param notification the notification to send.
     */
    public void sendNotification(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                NotificationResponseDTO responseDTO = NotificationResponseDTO.fromNotification(notification, notificationHandlerFactory);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(responseDTO));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }
}
