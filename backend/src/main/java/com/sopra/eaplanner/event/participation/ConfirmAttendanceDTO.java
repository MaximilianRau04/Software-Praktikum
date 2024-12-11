package com.sopra.eaplanner.event.participation;

import jakarta.validation.constraints.NotNull;

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
