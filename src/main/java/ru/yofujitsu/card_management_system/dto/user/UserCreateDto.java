package ru.yofujitsu.card_management_system.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserCreateDto(
        @NotNull String username,
        @NotNull @Email String email,
        @NotNull String password
) {
}
