package ru.yofujitsu.card_management_system.dto.auth;

import jakarta.validation.constraints.NotNull;

public record SignInRequestDto(
        @NotNull String username,
        @NotNull String password
) {
}
