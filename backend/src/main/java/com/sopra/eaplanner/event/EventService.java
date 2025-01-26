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

    /**
     * Creates a new event based on the provided request body.
     * The method checks if the event date is within the given exchange day's range.
     * A QR code is generated and associated with the event.
     *
     * @param requestBody the event details to be used for creating the event.
     * @return a response DTO containing the saved event data.
     * @throws Exception if an error occurs while creating the event or generating the QR code.
     */
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

    /**
     * Confirms the attendance of a user for the specified event using the provided token.
     * The user must be registered for the event in order for attendance to be confirmed.
     *
     * @param eventId the ID of the event for which attendance is being confirmed.
     * @param requestBody the attendance confirmation data, including the user ID and token.
     */
    public void confirmAttendance(Long eventId, ConfirmAttendanceDTO requestBody) {
        Event event = validateEventToken(eventId, requestBody.getToken());
        User user = userRepository.findById(requestBody.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!event.getRegisteredUsers().contains(user)) {
            throw new UserNotRegisteredException("User was not registered to this event.");
        }

        eventParticipationService.confirmAttendance(user, event);
    }

    /**
     * Retrieves a list of all events in the system.
     *
     * @return a list of response DTOs containing all event data.
     */
    public List<EventResponseDTO> getAllEvents() {
        Iterable<Event> events = eventRepository.findAll();
        List<EventResponseDTO> eventDTOs = new ArrayList<>();

        for (Event event : events) {
            eventDTOs.add(new EventResponseDTO(event));
        }
        return eventDTOs;
    }

    /**
     * Retrieves events based on a list of tag names.
     * The events returned must contain all of the specified tags.
     *
     * @param tagNames the names of the tags to filter events by.
     * @return a list of event response DTOs for the events that match the provided tags.
     */
    public List<EventResponseDTO> getEventsFromTags(List<String> tagNames) {
        return tagRepository.findByTagsNames(tagNames, (long) tagNames.size())
                .stream().map(EventResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the event details for the specified event ID.
     *
     * @param id the ID of the event to be retrieved.
     * @return a response DTO containing the event details.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new EventResponseDTO(event);
    }

    /**
     * Retrieves a list of users who are registered for the specified event.
     *
     * @param id the ID of the event.
     * @return a list of user response DTOs for the users registered for the event.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public Iterable<UserResponseDTO> getRegisteredUsers(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return event.getRegisteredUsers()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a set of forum threads associated with the specified event.
     *
     * @param id the ID of the event.
     * @return a set of response DTOs for the forum threads linked to the event.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public Set<ForumThreadResponseDTO> getForumThreads(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return event.getForumThreads().stream().map(ForumThreadResponseDTO::new).collect(Collectors.toSet());
    }

    /**
     * Retrieves the location of the event associated with the provided event ID.
     *
     * @param id the ID of the event.
     * @return a DTO containing the event location.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public LocationDTO getLocation(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new LocationDTO(event.getExchangeDay().getLocation());
    }

    /**
     * Retrieves the organizer details for the specified event.
     *
     * @param id the ID of the event.
     * @return a user response DTO for the event organizer.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public UserResponseDTO getOrganizerByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new UserResponseDTO(event.getOrganizer());
    }

    /**
     * Retrieves the exchange day associated with the specified event.
     *
     * @param id the ID of the event.
     * @return a response DTO containing the exchange day details.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public ExchangeDayResponseDTO getExchangeDayByEventId(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return new ExchangeDayResponseDTO(event.getExchangeDay());
    }

    /**
     * Retrieves a list of resources associated with the specified event.
     *
     * @param id the ID of the event.
     * @return a list of resource response DTOs for the resources linked to the event.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
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

    /**
     * Retrieves a set of tags associated with the specified event.
     *
     * @param eventId the ID of the event.
     * @return a set of response DTOs for the tags linked to the event.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public Set<TagResponseDTO> getTagsFromEvent(Long eventId) {
        Event eventWithTags = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        return eventWithTags.getTags().stream().map(TagResponseDTO::new).collect(Collectors.toSet());
    }

    /**
     * Retrieves all available experience levels for events.
     *
     * @return a list of experience level names.
     */
    public List<String> getAllExperienceLevels() {
        return Arrays.stream(ExperienceLevel.values())
                .map(Enum::name)
                .toList();
    }

    /**
     * Updates an existing event based on the provided request body.
     * The method checks if the event date is within the given exchange day's range.
     *
     * @param id the ID of the event to be updated.
     * @param requestBody the new event details to update the event with.
     * @return a response DTO containing the updated event data.
     * @throws EntityNotFoundException if no event or exchange day is found with the provided ID.
     */
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

        eventRepository.save(event);
        return new EventResponseDTO(event);
    }

    /**
     * Deletes an event by its ID.
     * This also removes the event from the registered users' event lists.
     *
     * @param id the ID of the event to be deleted.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        for (User user : event.getRegisteredUsers()) {
            user.getRegisteredEvents().remove(event);
        }

        eventRepository.deleteById(id);
    }

    /**
     * Retrieves the QR code for the specified event.
     * The QR code is stored as a file and is returned as a resource.
     *
     * @param id the ID of the event.
     * @return the QR code file as a resource.
     * @throws IOException if an error occurs while reading the QR code file.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public Resource getQRCode(Long id) throws IOException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found."));
        Path filePath = Paths.get(event.getQrCodeFilePath());
        Resource file = new UrlResource(filePath.toUri());

        if (!file.exists()) {
            throw new EntityNotFoundException("File not found.");
        }

        return file;
    }

    /**
     * Retrieves the attendance token for the specified event.
     *
     * @param eventId the ID of the event.
     * @return a map containing the attendance token.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    public Map<String, String> getAttendanceToken(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found."));

        Map<String, String> response = new HashMap<>();
        response.put("attendanceToken", event.getAttendanceToken());
        return response;
    }

    /**
     * Generates and saves a QR code for the given event.
     * The QR code is saved as a PNG file and associated with the event.
     *
     * @param requestBody the event for which the QR code is being generated.
     * @throws Exception if an error occurs while generating or saving the QR code.
     */
    private void generateAndSaveQRCode(Event requestBody) throws Exception {
        String eventUrl = generateEventUrl(requestBody.getId(), requestBody.getAttendanceToken());
        byte[] qrCode = QRCodeService.getQRCodeImage(eventUrl, 300, 300);

        String fileName = "qrcode_event_" + requestBody.getId() + ".png";
        QRCodeService.saveQRCode(qrCode, fileName);

        requestBody.setQrCodeFilePath(QRCodeService.QR_CODE_DIR + "/" + fileName);
    }

    /**
     * Validates the attendance token for the specified event.
     * If the token is invalid, an exception is thrown.
     *
     * @param eventId the ID of the event.
     * @param token the attendance token to validate.
     * @return the event if the token is valid.
     * @throws InvalidTokenException if the token is invalid or expired.
     * @throws EntityNotFoundException if no event is found with the provided ID.
     */
    private Event validateEventToken(Long eventId, String token) {
        Event eventToValidate = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found."));

        if (!eventToValidate.getAttendanceToken().equals(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        return eventToValidate;
    }

    // TODO: Swap to use this for attendance link QR frontend
    /**
     * Generates the URL for an event's attendance page, which includes the event ID and token.
     *
     * @param eventId the ID of the event.
     * @param token the attendance token.
     * @return the generated URL.
     */
    private String generateEventUrl(Long eventId, String token) {
        return EVENT_ENDPOINT + eventId + TOKEN_SUFFIX + token;
    }

    /**
     * Checks if the provided date is outside the start and end date of the given exchange day.
     *
     * @param exchangeDay the exchange day to check the date against.
     * @param date the date to be checked.
     * @return true if the date is outside the exchange day range, otherwise false.
     */
    private boolean isDateOutsideExchangeDay(ExchangeDay exchangeDay, LocalDate date) {
        return exchangeDay.getStartDate().isAfter(date) || exchangeDay.getEndDate().isBefore(date);
    }
}