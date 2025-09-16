package ru.yofujitsu.card_management_system.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.util.CardUtils;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final CardUtils cardUtils;

    public CardDto toCardDto(Card card) {
        return new CardDto(
                card.getId(),
                cardUtils.maskCardNumber(card.getCardNumber()),
                card.getCardHolder(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance(),
                card.getUser().getId()
        );
    }
}
