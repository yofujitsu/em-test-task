package ru.yofujitsu.card_management_system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignUpRequestDto(
        @NotNull String username,
        @NotNull @Email String email,
        @NotNull String password
) {
}
