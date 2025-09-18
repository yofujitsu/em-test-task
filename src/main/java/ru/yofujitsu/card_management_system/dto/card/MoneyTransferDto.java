package ru.yofujitsu.card_management_system.dto.card;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MoneyTransferDto(
        @NotNull String fromCardNumber,
        @NotNull String toCardNumber,
        @Positive double amount
) {
}