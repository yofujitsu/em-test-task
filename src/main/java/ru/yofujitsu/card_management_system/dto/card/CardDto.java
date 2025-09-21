package ru.yofujitsu.card_management_system.dto.card;

import lombok.Builder;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;

import java.util.UUID;

@Builder
public record CardDto(
        UUID id,
        String cardNumber,
        String cardHolder,
        String expiryDate,
        CardStatus cardStatus,
        double balance,
        UUID userId
) {
}