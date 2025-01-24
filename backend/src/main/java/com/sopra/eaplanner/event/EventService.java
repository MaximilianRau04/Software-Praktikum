package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.event.tags.TagRepository;
import com.sopra.eaplanner.event.tags.TagResponseDTO;
import com.sopra.eaplanner.event.tags.TagService;
import com.sopra.eaplanner.exceptions.InvalidTokenException;
import com.sopra.eaplanner.exceptions.UserNotRegisteredException;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayRepository;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.forumthread.ForumThreadResponseDTO;
import com.sopra.eaplanner.locations.LocationDTO;
import com.sopra.eaplanner.qrcode.QRCodeService;
import com.sopra.eaplanner.resource.ResourceItem;
import com.sopra.eaplanner.resource.dtos.ResourceResponse;
import com.sopra.eaplanner.resource.ResourceType;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.user.UserService;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    private static final String EVENT_ENDPOINT = "http://193.196.54.172:8000/events/";
    private static final String TOKEN_SUFFIX = "/attendance?token=";

    @Autowired
    private ExchangeDayRepository exchangeDayRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    public EventResponseDTO createEvent(EventRequestDTO requestBody) throws Exception {
        ExchangeDay exchangeDay = exchangeDayRepository.findById(requestBody.getExchangeDayId())
                .orElseThrow(() -> new EntityNotFoundException("ExchangeDay not found."));

        if (isDateOutsideExchangeDay(exchangeDay, requestBody.getDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Event Date must be during Exchange day: "
                            + exchangeDay.getStartDate() + " to " + exchangeDay.getEndDate());
        }

        User eventOrganizer = userRepository.findById(requestBody.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found."));

        Event savedEvent = eventRepository.save(
                new Event(requestBody,
                        exchangeDay,
                        eventOrganizer,
                        tagService.mergeAndGetTagsFromRequest(requestBody.getTags())));

        userService.registerUserToEvent(eventOrganizer.getId(), savedEvent.getId()); // TODO: Implement proper organizer handling
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

    public List<EventResponseDTO> getEventsFromTags(List<String> tagNames) {
        return tagRepository.findByTagsNames(tagNames, (long) tagNames.size())
                .stream().map(EventResponseDTO::new)
                .collect(Collectors.toList());
    }

    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new EventResponseDTO(event);
    }

    public Iterable<UserResponseDTO> getRegisteredUsers(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return event.getRegisteredUsers()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Set<ForumThreadResponseDTO> getForumThreads(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return event.getForumThreads().stream().map(ForumThreadResponseDTO::new).collect(Collectors.toSet());
    }

    public LocationDTO getLocation(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new LocationDTO(event.getExchangeDay().getLocation());
    }

    public UserResponseDTO getOrganizerByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new UserResponseDTO(event.getOrganizer());
    }

    public ExchangeDayResponseDTO getExchangeDayByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new ExchangeDayResponseDTO(event.getExchangeDay());
    }

    public List<ResourceResponse> getResourcesByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        List<ResourceResponse> resourceDTOs = new ArrayList<>();

        for (ResourceItem resource : event.getResources()) {
            resourceDTOs.add(new ResourceResponse(
                    resource.getId(),
                    resource.getName(),
                    resource.getType().toString(),
                    resource.getDescription(),
                    resource.getLocation(),
                    resource.getAvailability(),
                    resource.getType() == ResourceType.ROOM ? resource.getCapacity() : null
            ));
        }

        return resourceDTOs;
    }

    public Set<TagResponseDTO> getTagsFromEvent(Long eventId) {
        Event eventWithTags = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return eventWithTags.getTags().stream().map(TagResponseDTO::new).collect(Collectors.toSet());
    }

    public List<String> getAllExperienceLevels() {
        return Arrays.stream(ExperienceLevel.values())
                .map(Enum::name)
                .toList();
    }

    public EventResponseDTO updateEvent(Long id, EventRequestDTO requestBody) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        ExchangeDay exchangeDay = exchangeDayRepository.findById(requestBody.getExchangeDayId())
                .orElseThrow(() -> new EntityNotFoundException("ExchangeDay not found."));

        if (isDateOutsideExchangeDay(exchangeDay, requestBody.getDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Event Date must be during Exchange day: "
                            + exchangeDay.getStartDate() + " to " + exchangeDay.getEndDate());
        }

        event.setName(requestBody.getName());
        event.setDate(requestBody.getDate());
        event.setStartTime(requestBody.getStartTime());
        event.setEndTime(requestBody.getEndTime());
        event.setRoom(requestBody.getRoom());
        event.setDescription(requestBody.getDescription());
        event.setExchangeDay(exchangeDay);
        event.setOrganizer(userRepository.findById(requestBody.getOrganizerId())
                .orElseThrow(() -> new EntityNotFoundException("Organizer not found.")));
        event.setRecommendedExperience(requestBody.getRecommendedExperience());
        event.setTags(tagService.mergeAndGetTagsFromRequest(requestBody.getTags()));
        event.setTags(tagService.mergeAndGetTagsFromRequest(requestBody.getTags()));

        eventRepository.save(event);
        return new EventResponseDTO(event);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        for (User user : event.getRegisteredUsers()) {
            user.getRegisteredEvents().remove(event);
        }

        eventRepository.deleteById(id);
    }

    public Resource getQRCode(Long id) throws IOException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        Path filePath = Paths.get(event.getQrCodeFilePath());
        Resource file = new UrlResource(filePath.toUri());

        if (!file.exists()) {
            throw new EntityNotFoundException("File not found.");
        }

        return file;
    }

    public Map<String, String> getAttendanceToken(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found."));

        Map<String, String> response = new HashMap<>();
        response.put("attendanceToken", event.getAttendanceToken());
        return response;
    }

    private void generateAndSaveQRCode(Event requestBody) throws Exception {
        String eventUrl = generateEventUrl(requestBody.getId(), requestBody.getAttendanceToken());
        byte[] qrCode = QRCodeService.getQRCodeImage(eventUrl, 300, 300);

        String fileName = "qrcode_event_" + requestBody.getId() + ".png";
        QRCodeService.saveQRCode(qrCode, fileName);

        requestBody.setQrCodeFilePath(QRCodeService.QR_CODE_DIR + "/" + fileName);
    }

    private Event validateEventToken(Long eventId, String token) {
        Event eventToValidate = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        if (!eventToValidate.getAttendanceToken().equals(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        return eventToValidate;
    }

    // TODO: Swap to use this for attendance link QR frontend
    private String generateEventUrl(Long eventId, String token) {
        return EVENT_ENDPOINT + eventId + TOKEN_SUFFIX + token;
    }

    private boolean isDateOutsideExchangeDay(ExchangeDay exchangeDay, LocalDate date) {
        return exchangeDay.getStartDate().isAfter(date) || exchangeDay.getEndDate().isBefore(date);
    }
}