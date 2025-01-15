package com.sopra.eaplanner.event;

import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.event.participation.ConfirmAttendanceDTO;
import com.sopra.eaplanner.event.participation.EventParticipation;
import com.sopra.eaplanner.event.participation.EventParticipationRepository;
import com.sopra.eaplanner.event.participation.EventParticipationService;
import com.sopra.eaplanner.exchangeday.ExchangeDay;
import com.sopra.eaplanner.exchangeday.ExchangeDayRepository;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserRepository;
import com.sopra.eaplanner.exceptions.InvalidTokenException;
import com.sopra.eaplanner.exceptions.UserNotRegisteredException;
import com.sopra.eaplanner.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebAppConfiguration
@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ExchangeDayRepository exchangeDayRepository;

    @MockBean
    private EventParticipationRepository eventParticipationRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private EventParticipationService eventParticipationService;

    private Event mockEvent;
    private User mockUser;
    private ExchangeDay mockExchangeDay;
    private EventParticipation mockEventParticipation;

    private static final String TEST_QR_CODE_PATH = System.getProperty("user.home") + "/Downloads/qrcode_event_1.png";

    @BeforeEach
    void setUp() {
        mockEvent = new Event();
        mockEvent.setId(1L);
        mockEvent.setName("Test Event");
        mockEvent.setAttendanceToken("validToken");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");

        mockEvent.getRegisteredUsers().add(mockUser);
        mockUser.getRegisteredEvents().add(mockEvent);

        mockExchangeDay = new ExchangeDay();
        mockExchangeDay.setId(1L);
        mockExchangeDay.setName("Test Exchange Day");

        mockEventParticipation = new EventParticipation();
        mockEventParticipation.setId(1L);
        mockEventParticipation.setEvent(mockEvent);
        mockEventParticipation.setUser(mockUser);
    }

    @Test
    void testCreateEventSuccess() throws Exception {
        // Arrange
        EventRequestDTO eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setName("Test Event");
        eventRequestDTO.setExchangeDayId(mockExchangeDay.getId());
        eventRequestDTO.setOrganizerId(mockUser.getId());

        when(exchangeDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockExchangeDay));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);
        when(userService.registerUserToEvent(any(Long.class),any(Long.class))).thenReturn(mockUser); //TODO organizer handling

        // Act
        EventResponseDTO result = eventService.createEvent(eventRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Event", result.getName());
        verify(eventRepository, times(2)).save(any(Event.class));
    }

    @Test
    void testCreateEventExchangeDayNotFound() {
        // Arrange
        EventRequestDTO eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setName("Test Event");
        eventRequestDTO.setExchangeDayId(mockExchangeDay.getId());
        eventRequestDTO.setOrganizerId(mockUser.getId());

        when(exchangeDayRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.createEvent(eventRequestDTO));
    }

    @Test
    void testCreateEventOrganizerNotFound() {
        // Arrange
        EventRequestDTO eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setName("Test Event");
        eventRequestDTO.setExchangeDayId(mockExchangeDay.getId());
        eventRequestDTO.setOrganizerId(mockUser.getId());

        when(exchangeDayRepository.findById(any(Long.class))).thenReturn(Optional.of(mockExchangeDay));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.createEvent(eventRequestDTO));
    }

    @Test
    void testConfirmAttendanceSuccess() {
        // Arrange
        ConfirmAttendanceDTO confirmAttendanceDTO = new ConfirmAttendanceDTO();
        confirmAttendanceDTO.setUserId(mockUser.getId());
        confirmAttendanceDTO.setToken("validToken");


        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));
        when(eventParticipationRepository.findByUserAndEvent(any(User.class), any(Event.class)))
                .thenReturn(Optional.of(mockEventParticipation));

        // Act
        eventService.confirmAttendance(mockEvent.getId(), confirmAttendanceDTO);

        // Assert
        verify(eventParticipationRepository, times(1)).save(any(EventParticipation.class));
        assertTrue(mockEventParticipation.getIsParticipationConfirmed());
    }

    @Test
    void testConfirmAttendanceUpdatesParticipation() {
        // Arrange
        EventParticipation mockParticipation = new EventParticipation();
        mockParticipation.setId(1L);
        mockParticipation.setUser(mockUser);
        mockParticipation.setEvent(mockEvent);

        when(eventParticipationRepository.findByUserAndEvent(any(User.class), any(Event.class)))
                .thenReturn(Optional.of(mockParticipation));

        ArgumentCaptor<EventParticipation> captor = ArgumentCaptor.forClass(EventParticipation.class);

        // Act
        eventParticipationService.confirmAttendance(mockUser, mockEvent);

        // Assert
        verify(eventParticipationRepository, times(1)).save(captor.capture());

        EventParticipation savedParticipation = captor.getValue();

        assertNotNull(savedParticipation, "The saved participation should not be null");
        assertTrue(savedParticipation.getIsParticipationConfirmed(), "Participation should be confirmed");
        assertNotNull(savedParticipation.getConfirmationTime(), "Confirmation time should be set");
    }


    @Test
    void testConfirmAttendanceUserNotRegistered() {
        // Arrange
        ConfirmAttendanceDTO confirmAttendanceDTO = new ConfirmAttendanceDTO();
        confirmAttendanceDTO.setUserId(mockUser.getId());
        confirmAttendanceDTO.setToken("validToken");

        mockEvent.getRegisteredUsers().remove(mockUser);
        mockUser.getRegisteredEvents().remove(mockEvent);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        // Act & Assert
        assertThrows(UserNotRegisteredException.class, () -> eventService.confirmAttendance(mockEvent.getId(), confirmAttendanceDTO));
    }

    @Test
    void testConfirmAttendanceInvalidToken() {
        // Arrange
        ConfirmAttendanceDTO confirmAttendanceDTO = new ConfirmAttendanceDTO();
        confirmAttendanceDTO.setUserId(mockUser.getId());
        confirmAttendanceDTO.setToken("invalidToken");

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> eventService.confirmAttendance(mockEvent.getId(), confirmAttendanceDTO));
    }


    @Test
    void testGetQRCode_Success() throws IOException {
        // Arrange
        Long eventId = 1L;
        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setQrCodeFilePath(TEST_QR_CODE_PATH);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        Path filePath = Paths.get(TEST_QR_CODE_PATH);
        Files.createDirectories(filePath.getParent());

        Files.deleteIfExists(filePath);

        Files.createFile(filePath);

        // Act
        Resource qrCodeResource = eventService.getQRCode(eventId);

        // Assert
        assertNotNull(qrCodeResource, "The QR code resource should not be null.");
        assertTrue(qrCodeResource.exists(), "The QR code file should exist.");
        assertEquals(filePath.toUri(), qrCodeResource.getURI(), "The file URI should match the QR code file path.");

        // Cleanup
        Files.deleteIfExists(filePath);
    }

    @Test
    void testGetQRCode_EventNotFound() {
        // Arrange
        Long eventId = 99L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> eventService.getQRCode(eventId));
        assertEquals("Event not found.", exception.getMessage(), "Exception message should indicate event not found.");
    }

    @Test
    void testGetQRCode_FileNotFound() throws IOException {
        // Arrange
        Long eventId = 1L;
        Event mockEvent = new Event();
        mockEvent.setId(eventId);
        mockEvent.setQrCodeFilePath(TEST_QR_CODE_PATH);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        Path filePath = Paths.get(TEST_QR_CODE_PATH);
        Files.deleteIfExists(filePath);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> eventService.getQRCode(eventId));
        assertEquals("File not found.", exception.getMessage(), "Exception message should indicate file not found.");
    }

    @Test
    void testGetQRCodeEventNotFound() {
        // Arrange
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.getQRCode(mockEvent.getId()));
    }

    @Test
    void testDeleteEvent() {
        // Arrange
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        // Act
        eventService.deleteEvent(mockEvent.getId());

        // Assert
        verify(eventRepository, times(1)).deleteById(mockEvent.getId());
    }

    @Test
    void testDeleteEventNotFound() {
        // Arrange
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> eventService.deleteEvent(mockEvent.getId()));
    }
}
