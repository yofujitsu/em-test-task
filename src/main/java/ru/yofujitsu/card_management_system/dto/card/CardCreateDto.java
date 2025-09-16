package ru.yofujitsu.card_management_system.dto.card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CardCreateDto(
        @NotNull String cardNumber,
        @NotNull String cardHolder,
        @NotNull String expiryDate,
        @PositiveOrZero double balance
) {
}
