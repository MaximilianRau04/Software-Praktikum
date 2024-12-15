package com.sopra.eaplanner.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopra.eaplanner.user.dtos.UserRequestDTO;
import com.sopra.eaplanner.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        // Arrange
        UserRequestDTO userRequestDTO = UserRequestDTO.mockWith("deeniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        UserResponseDTO userResponseDTO = UserResponseDTO.mockWith(1L, "deeniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("deeniz"))
                .andExpect(jsonPath("$.firstname").value("Deniz"))
                .andExpect(jsonPath("$.lastname").value("Altunkapan"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponseDTO userResponseDTO = UserResponseDTO.mockWith(1L, "deeniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        when(userService.getUserById(userId)).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("deeniz"))
                .andExpect(jsonPath("$.firstname").value("Deniz"))
                .andExpect(jsonPath("$.lastname").value("Altunkapan"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testGetUserByUsername() throws Exception {
        // Arrange
        String username = "deeniz";
        UserResponseDTO userResponseDTO = UserResponseDTO.mockWith(1L, "deeniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        when(userService.getUserByUsername(username)).thenReturn(userResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/search?username={username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstname").value("Deniz"))
                .andExpect(jsonPath("$.lastname").value("Altunkapan"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        Long userId = 1L;
        UserRequestDTO userRequestDTO = UserRequestDTO.mockWith("deeniz", "Deniz", "Altunkapan", User.Role.ADMIN);

        UserResponseDTO updatedUser = UserResponseDTO.mockWith(1L, "deeniz", "Alter", "Altunkapan", User.Role.ADMIN);

        when(userService.updateUser(any(Long.class), any(UserRequestDTO.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("deeniz"))
                .andExpect(jsonPath("$.firstname").value("Alter"))
                .andExpect(jsonPath("$.lastname").value("Altunkapan"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).updateUser(eq(userId), any(UserRequestDTO.class));
    }

    // Test for DELETE /api/users/{id} (Delete User)
    @Test
    void testDeleteUser() throws Exception {
        // Arrange
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }
}
