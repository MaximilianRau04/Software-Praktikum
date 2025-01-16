package com.sopra.eaplanner.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopra.eaplanner.feedback.dtos.FeedbackRequestDTO;
import com.sopra.eaplanner.feedback.dtos.FeedbackResponseDTO;
import com.sopra.eaplanner.user.User;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFeedback() throws Exception {
        // Arrange
        FeedbackRequestDTO feedbackRequestDTO = FeedbackRequestDTO.mockWith(5, 4, 3, 4, 4, 5, 4, 3, 4, 5, 3, 4, 3, 4, 3, 4, 3,
                "Enjoyed the event", "Could be better", "Looking forward to the next one", "I learned a lot",
                true, "Very good", 4, false, 1L, 1L);

        FeedbackResponseDTO feedbackResponseDTO = FeedbackResponseDTO.mockWith(5, 4, 3, 4, 4, 5, 4, 3, 4, 5, 3, 4, 3, 4, 3, 4,
                3, "Enjoyed the event", "Could be better", "Looking forward to the next one",
                "I learned a lot", true, "Very good",4);

        when(feedbackService.createFeedback(any(FeedbackRequestDTO.class))).thenReturn(feedbackResponseDTO);

        // Act
        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.overallScore").value(5))
                .andExpect(jsonPath("$.organisationalScore").value(4))
                .andExpect(jsonPath("$.relevanceScore").value(3));

        verify(feedbackService, times(1)).createFeedback(any(FeedbackRequestDTO.class));
    }

    @Test
    void testGetFeedbackById() throws Exception {
        // Arrange
        Long feedbackId = 1L;
        FeedbackResponseDTO feedbackResponseDTO = FeedbackResponseDTO.mockWith(5, 4, 3, 4, 4, 5, 4, 3, 4, 5, 3, 4, 3, 4, 3, 4,
                3, "Enjoyed the event", "Could be better", "Looking forward to the next one",
                "I learned a lot", true, "Very good",4);

        when(feedbackService.getFeedbackById(feedbackId)).thenReturn(feedbackResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/feedback/{id}", feedbackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallScore").value(5))
                .andExpect(jsonPath("$.organisationalScore").value(4));

        verify(feedbackService, times(1)).getFeedbackById(feedbackId);
    }

    @Test
    void testGetAllFeedback() throws Exception {
        // Arrange
        FeedbackResponseDTO firstFeedbackResponseDTO = FeedbackResponseDTO.mockWith(5, 4, 3, 4, 4, 5, 4, 3, 4, 5, 3, 4, 3, 4, 3, 4,
                3, "Enjoyed the event", "Could be better", "Looking forward to the next one",
                "I learned a lot", true, "Very good",4);

        FeedbackResponseDTO secondFeedbackResponseDTO = FeedbackResponseDTO.mockWith(1, 4, 3, 4, 4, 5, 4, 3, 4, 5, 3, 4, 3, 4, 3, 4,
                3, "Enjoyed the event", "Could be better", "Looking forward to the next one",
                "I learned a lot", true, "Very good",4);

        when(feedbackService.getAllFeedbacks()).thenReturn(List.of(firstFeedbackResponseDTO, secondFeedbackResponseDTO));

        // Act & Assert
        mockMvc.perform(get("/api/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].overallScore").value(5))
                .andExpect(jsonPath("$[1].overallScore").value(1));

        verify(feedbackService, times(1)).getAllFeedbacks();
    }

    @Test
    void testGetFeedbackAuthor() throws Exception {
        // Arrange
        Long feedbackId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.mockWith(1L, "döniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        when(feedbackService.getFeedbackAuthor(feedbackId)).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/feedback/{id}/author", feedbackId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstname").value("Deniz"))
                .andExpect(jsonPath("$.lastname").value("Altunkapan"));

        verify(feedbackService, times(1)).getFeedbackAuthor(feedbackId);
    }

    @Test
    void testDeleteFeedback() throws Exception {
        // Arrange
        Long feedbackId = 1L;

        doNothing().when(feedbackService).deleteFeedback(feedbackId);

        // Act & Assert
        mockMvc.perform(delete("/api/feedback/{id}", feedbackId))
                .andExpect(status().isNoContent());

        verify(feedbackService, times(1)).deleteFeedback(feedbackId);
    }
}
