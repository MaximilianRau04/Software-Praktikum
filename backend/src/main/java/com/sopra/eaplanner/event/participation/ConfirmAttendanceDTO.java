package com.sopra.eaplanner.event.participation;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for confirming user attendance at an event.
 *
 * <p>This class is used to encapsulate the data required to confirm attendance, typically
 * including a unique token and the ID of the user. It ensures that both fields are provided
 * and valid before processing.</p>
 *
 * <p>Validation annotations are applied to enforce that neither the token nor the user ID
 * can be null. This DTO is commonly used in API requests related to attendance confirmation.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 *   ConfirmAttendanceDTO dto = new ConfirmAttendanceDTO("some-token", 123L);
 *   System.out.println(dto.getToken()); // Outputs: some-token
 * </pre>
 */
public class ConfirmAttendanceDTO {

    @NotNull
    private String token;

    @NotNull
    private Long userId;

    public ConfirmAttendanceDTO() {
    }

    public ConfirmAttendanceDTO(String token, Long userId) {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
