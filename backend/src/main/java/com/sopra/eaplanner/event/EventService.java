package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.exceptions.InvalidTokenException;
import com.sopra.eaplanner.exceptions.UserNotRegisteredException;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayRepository;
import com.sopra.eaplanner.exchangeday.ExchangeDayService;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.qrcode.QRCodeService;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.UserService;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

    @Autowired
    private ExchangeDayService exchangeDayService;

    public EventResponseDTO createEvent(EventRequestDTO event) throws Exception {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(event.getExchangeDayId())
                .orElseThrow(() -> new EntityNotFoundException("ExchangeDay not found."));

        User eventOrganizer = userRepository.findById(event.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found."));

        Event savedEvent = eventRepository.save(new Event(event, exchangeDay, eventOrganizer));
        generateAndSaveQRCode(savedEvent);
        eventRepository.save(savedEvent);

        return new EventResponseDTO(savedEvent);
    }

    public void confirmAttendance(Long eventId, ConfirmAttendanceDTO requestBody) {

        Event event = validateEventToken(eventId, requestBody.getToken());
        User user = userRepository.findById(requestBody.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!event.getRegisteredUsers().contains(user)) {
            throw new UserNotRegisteredException("User was not registered to this event.");
        }

        eventParticipationService.confirmAttendance(user, event);
    }

    public List<EventResponseDTO> getAllEvents() {
        Iterable<Event> events = eventRepository.findAll();
        List<EventResponseDTO> eventDTOs = new ArrayList<>();

        for (Event event : events) {
            eventDTOs.add(new EventResponseDTO(event));
        }
        return eventDTOs;
    }

    public EventResponseDTO getEventById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found.");
        }
        return new EventResponseDTO(eventRepository.findById(id).get());
    }

    public Iterable<UserResponseDTO> getRegisteredUsers(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found");
        }
        return eventRepository.findById(id)
                .get()
                .getRegisteredUsers()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getOrganizerByEventId(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found");
        }
        return userService.getUserById(id);
    }

    public ExchangeDayResponseDTO getExchangeDayByEventId(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event not found");
        }
        return exchangeDayService.getExchangeDayById(id);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        for (User user : event.getRegisteredUsers()) {
            user.getRegisteredEvents().remove(event);
        }

        eventRepository.deleteById(id);
    }

    public Resource getQRCode(Long eventId) throws IOException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        Path filePath = Paths.get(event.getQrCodeFilePath());
        Resource file = new UrlResource(filePath.toUri());

        if (!file.exists()) {
            throw new EntityNotFoundException("File not found.");
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
        Event eventToValidate = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        if (!eventToValidate.getAttendanceToken().equals(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        return eventToValidate;
    }

    private String generateEventUrl(Long eventId, String token) {
        return EVENT_ENDPOINT + eventId + TOKEN_SUFFIX + token;
    }
}