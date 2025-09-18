package ru.yofujitsu.card_management_system.dto.card;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardBlockRequestDto(
        @NotNull UUID cardId,
        @NotNull String username
) {
}
