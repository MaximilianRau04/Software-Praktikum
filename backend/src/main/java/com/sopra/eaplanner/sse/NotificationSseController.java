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

@RestController
@RequestMapping("/sse")
public class NotificationSseController {

    @Autowired
    private NotificationHandlerFactory notificationHandlerFactory;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/notifications/{userId}")
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

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
