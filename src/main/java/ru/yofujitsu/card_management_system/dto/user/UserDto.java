package ru.yofujitsu.card_management_system.dto.user;

import lombok.Builder;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.entity.user.UserRole;

import java.util.List;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String email,
        String username,
        UserRole role,
        List<CardDto> cards
) {
}
