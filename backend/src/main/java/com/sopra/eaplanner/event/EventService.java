package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayRepository;
import com.sopra.eaplanner.qrcode.QRCodeService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    // "http://193.196.54.172:8000/events/";
    private static final String EVENT_ENDPOINT = "http://localhost:8080/events/";
    private static final String TOKEN_SUFFIX = "/attendance?token=";

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeDayRepository exchangeDayRepository;

    public Event createEvent(EventDTO event) throws Exception {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(event.getExchangeDayId())
                .orElseThrow(() -> new RuntimeException("ExchangeDay not found."));

        User eventOrganizer = userRepository.findById(event.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found."));

        List<User> registeredUsers = event.getRegisteredUserIds()
                .stream()
                .map((id) -> userRepository.findById(id).orElseThrow(() -> new RuntimeException("Registered User not found.")))
                .toList();

        Event savedEvent = eventRepository.save(new Event(event, exchangeDay, eventOrganizer, registeredUsers));
        generateAndSaveQRCode(savedEvent);

        return eventRepository.save(savedEvent);
    }

    public void confirmAttendance(Long eventId, ConfirmAttendanceDTO requestBody) {

        System.out.println(requestBody.getToken());
        System.out.println(requestBody.getUserId());

        Event event = validateEventToken(eventId, requestBody.getToken());
        User user = userRepository.findById(requestBody.getUserId()).orElseThrow(() -> new RuntimeException("User not found."));

        if (!event.getRegisteredUsers().contains(user)) {
            System.out.println("user was not registered");
            throw new RuntimeException("User was not registered to this event.");
        }

        eventParticipationService.confirmAttendance(user, event);
    }

    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventDTO getEventWithUserIds(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found."));

        List<Long> registeredUserIds = event.getRegisteredUsers().stream()
                .map(User::getId)
                .toList();

        return new EventDTO(eventId, event, registeredUserIds);
    }

    public Resource getQRCode(Long eventId) throws IOException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found."));
        Path filePath = Paths.get(event.getQrCodeFilePath());
        Resource file = new UrlResource(filePath.toUri());

        if (!file.exists()) {
            throw new RuntimeException("File not found.");
        }

        return file;
    }

    private void generateAndSaveQRCode(Event event) throws Exception {
        String eventUrl = generateEventUrl(event.getId(), event.getAttendanceToken());
        byte[] qrCode = QRCodeService.getQRCodeImage(eventUrl, 300, 300);

        String fileName = "qrcode_event_" + event.getId() + ".png";
        QRCodeService.saveQRCode(qrCode, fileName);

        event.setQrCodeFilePath(QRCodeService.QR_CODE_DIR + "/" + fileName);
    }

    private Event validateEventToken(Long eventId, String token) {
        Event eventToValidate = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found."));

        if (!eventToValidate.getAttendanceToken().equals(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        return eventToValidate;
    }

    private String generateEventUrl(Long eventId, String token) {
        return EVENT_ENDPOINT + eventId + TOKEN_SUFFIX + token;
    }
}