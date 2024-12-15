package com.sopra.eaplanner.exchangeDay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopra.eaplanner.event.dtos.EventResponseDTO;
import com.sopra.eaplanner.exchangeday.ExchangeDayService;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayRequestDTO;
import com.sopra.eaplanner.exchangeday.dtos.ExchangeDayResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ExchangeDayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeDayService exchangeDayService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllExchangeDays() throws Exception {
        // Arrange
        ExchangeDayResponseDTO responseDTO1 = ExchangeDayResponseDTO.mockWith(1L, LocalDate.of(2024, 12, 15), "Event 1", "Room 101", "Event Description 1");
        ExchangeDayResponseDTO responseDTO2 = ExchangeDayResponseDTO.mockWith(2L, LocalDate.of(2024, 12, 16), "Event 2", "Room 102", "Event Description 2");
        when(exchangeDayService.getAllExchangeDays()).thenReturn(Arrays.asList(responseDTO1, responseDTO2));

        // Act & Assert
        mockMvc.perform(get("/api/exchange-days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Event 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Event 2"));
    }

    @Test
    void testGetExchangeDayById() throws Exception {
        // Arrange
        Long exchangeDayId = 1L;
        ExchangeDayResponseDTO responseDTO = ExchangeDayResponseDTO.mockWith(exchangeDayId, LocalDate.of(2024, 12, 15), "Event 1", "Room 101", "Event Description 1");
        when(exchangeDayService.getExchangeDayById(exchangeDayId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/exchange-days/{id}", exchangeDayId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exchangeDayId))
                .andExpect(jsonPath("$.name").value("Event 1"))
                .andExpect(jsonPath("$.location").value("Room 101"));
    }

    @Test
    void testCreateExchangeDay() throws Exception {
        // Arrange
        Long exchangeDayId = 1L;
        ExchangeDayRequestDTO requestDTO = ExchangeDayRequestDTO.mockWith(LocalDate.of(2045, 12, 30), "Event 1", "Room 101", "Event Description 1");
        ExchangeDayResponseDTO responseDTO = ExchangeDayResponseDTO.mockWith(exchangeDayId, LocalDate.of(2045, 12, 30), "Event 1", "Room 101", "Event Description 1");
        when(exchangeDayService.createExchangeDay(any(ExchangeDayRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/exchange-days")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/exchange-days/1"))
                .andExpect(jsonPath("$.id").value(exchangeDayId))
                .andExpect(jsonPath("$.name").value("Event 1"));
    }

    @Test
    void testUpdateExchangeDay() throws Exception {
        // Arrange
        Long exchangeDayId = 1L;
        ExchangeDayRequestDTO requestDTO = ExchangeDayRequestDTO.mockWith(LocalDate.of(2024, 12, 15), "Updated Event", "Room 102", "Updated Description");
        ExchangeDayResponseDTO responseDTO = ExchangeDayResponseDTO.mockWith(exchangeDayId, LocalDate.of(2024, 12, 15), "Updated Event", "Room 102", "Updated Description");
        when(exchangeDayService.updateExchangeDay(any(Long.class), any(ExchangeDayRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/exchange-days/{id}", exchangeDayId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exchangeDayId))
                .andExpect(jsonPath("$.name").value("Updated Event"))
                .andExpect(jsonPath("$.location").value("Room 102"));
    }

    @Test
    void testDeleteExchangeDay() throws Exception {
        // Arrange
        Long exchangeDayId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/exchange-days/{id}", exchangeDayId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetExchangeDayEvents() throws Exception {
        // Arrange
        Long exchangeDayId = 1L;
        EventResponseDTO eventDTO1 = EventResponseDTO.mockWith(1L, LocalTime.of(11, 0), LocalTime.of(12, 0), "Workshop A", "Room A", "Description A");
        EventResponseDTO eventDTO2 = EventResponseDTO.mockWith(2L, LocalTime.of(12, 0), LocalTime.of(13, 0), "Workshop B", "Room B", "Description B");
        when(exchangeDayService.getExchangeDayEvents(exchangeDayId)).thenReturn(Arrays.asList(eventDTO1, eventDTO2));

        // Act & Assert
        mockMvc.perform(get("/api/exchange-days/{id}/events", exchangeDayId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
