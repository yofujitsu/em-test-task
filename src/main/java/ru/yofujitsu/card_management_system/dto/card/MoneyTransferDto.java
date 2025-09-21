package ru.yofujitsu.card_management_system.dto.card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record MoneyTransferDto(
        @NotNull UUID fromCardId,
        @NotNull UUID toCardId,
        @Positive double amount
) {
}