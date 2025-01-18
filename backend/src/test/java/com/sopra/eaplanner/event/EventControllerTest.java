package com.sopra.eaplanner.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopra.eaplanner.event.dtos.EventRequestDTO;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.ExchangeDayService;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import com.sopra.eaplanner.feedback.FeedbackService;
import com.sopra.eaplanner.feedback.summary.CommentType;
import com.sopra.eaplanner.feedback.summary.FeedbackSummaryDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.UserService;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private ExchangeDayService exchangeDayService;

    @MockBean
    private UserService userService;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testGetAllEvents() throws Exception {
        List<EventResponseDTO> mockEvents = List.of(
                EventResponseDTO.mockWith(1L, LocalDate.of(2025, 01, 20), LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop A", "Room A", "Description A"),
                EventResponseDTO.mockWith(2L, LocalDate.of(2025, 01, 20), LocalTime.of(12, 0), LocalTime.of(13, 0), "Workshop B", "Room B", "Description B")
        );

        when(eventService.getAllEvents()).thenReturn(mockEvents);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Workshop A"))
                .andExpect(jsonPath("$[1].name").value("Workshop B"));

        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testGetEventById() throws Exception {
        EventResponseDTO mockEvent = EventResponseDTO.mockWith(1L, LocalDate.of(2025, 01, 20), LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop A", "Room A", "Description A");
        when(eventService.getEventById(mockEvent.getId())).thenReturn(mockEvent);

        mockMvc.perform(get("/api/events/{id}", mockEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockEvent.getId()))
                .andExpect(jsonPath("$.name").value("Workshop A"));

        verify(eventService, times(1)).getEventById(mockEvent.getId());
    }

    @Test
    void testCreateEvent() throws Exception {
        Long exchangeDayId = 1L;
        Long organizerId = 1L;
        Long trainerProfileId = 1L;

        ExchangeDayResponseDTO mockExchangeDay = ExchangeDayResponseDTO.mockWith(exchangeDayId, LocalDate.of(2045, 12, 30), LocalDate.of(2045, 12, 30), "Workshop A", "Berlin", "Description A");
        when(exchangeDayService.getExchangeDayById(exchangeDayId)).thenReturn(mockExchangeDay);

        UserResponseDTO mockOrganizer = UserResponseDTO.mockWith(organizerId, "admin", "Admin", "User", User.Role.ADMIN);
        when(userService.getUserById(organizerId)).thenReturn(mockOrganizer);

        EventRequestDTO eventRequest = EventRequestDTO.mockWith("Workshop A", LocalDate.of(2025, 01, 20), LocalTime.of(11, 0), LocalTime.of(12, 0), "Description A", "Room A", exchangeDayId, organizerId);

        EventResponseDTO eventResponse = EventResponseDTO.mockWith(1L, LocalDate.of(2025, 01, 20), LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop A", "Room A", "Description A");
        when(eventService.createEvent(any(EventRequestDTO.class))).thenReturn(eventResponse);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/events/" + eventResponse.getId()))
                .andExpect(jsonPath("$.id").value(eventResponse.getId()))
                .andExpect(jsonPath("$.name").value("Workshop A"))
                .andExpect(jsonPath("$.description").value("Description A"));

        verify(eventService, times(1)).createEvent(any(EventRequestDTO.class));
    }

    @Test
    void testGetFeedbackSummary() throws Exception {
        FeedbackSummaryDTO feedbackSummary = FeedbackSummaryDTO.mockWith(1L, "Workshop A", "Admin User");

        Map<String, FeedbackSummaryDTO.FeedbackStatistics> numericalFeedback = new HashMap<>();
        numericalFeedback.put("overallScore", new FeedbackSummaryDTO.FeedbackStatistics(4.5, 4.0, 100));
        feedbackSummary.setNumericalFeedback(numericalFeedback);

        List<FeedbackSummaryDTO.CommentAnalysis> commentAnalysis = new ArrayList<>();
        commentAnalysis.add(new FeedbackSummaryDTO.CommentAnalysis("Great workshop!", "positive", 1L));
        commentAnalysis.add(new FeedbackSummaryDTO.CommentAnalysis("Needs more practical examples.", "neutral", 2L));

        Map<CommentType,List<FeedbackSummaryDTO.CommentAnalysis>> comments = new HashMap<>();
        comments.put(CommentType.ENJOYMENT, commentAnalysis);
        feedbackSummary.setComments(comments);

        when(feedbackService.generateFeedbackSummary(1L)).thenReturn(feedbackSummary);

        mockMvc.perform(get("/api/events/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.eventName").value("Workshop A"))
                .andExpect(jsonPath("$.organizerName").value("Admin User"))
                .andExpect(jsonPath("$.numericalFeedback.overallScore.average").value(4.5))
                .andExpect(jsonPath("$.comments.ENJOYMENT[0].comment").value("Great workshop!"))
                .andExpect(jsonPath("$.comments.ENJOYMENT[1].sentiment").value("neutral"));

        verify(feedbackService, times(1)).generateFeedbackSummary(1L);
    }

    @Test
    void testDeleteEventById() throws Exception {
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEvent(1L);
    }

}
