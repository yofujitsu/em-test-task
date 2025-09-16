package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;
import ru.yofujitsu.card_management_system.exception.InactiveStatusException;
import ru.yofujitsu.card_management_system.exception.InvalidDepositException;
import ru.yofujitsu.card_management_system.mapper.CardMapper;
import ru.yofujitsu.card_management_system.repository.CardRepository;
import ru.yofujitsu.card_management_system.util.CardUtils;
import ru.yofujitsu.card_management_system.util.CardValidator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final CardValidator cardValidator;
    private final CardUtils cardUtils;
    private final CardMapper cardMapper;
    private final UserService userService;

    public void addNewCard(CardCreateDto cardCreateDto, UUID userId) {
        cardValidator.validateCard(cardCreateDto.cardNumber(), cardCreateDto.expiryDate());

        Card card = Card.builder()
                .cardNumber(cardCreateDto.cardNumber())
                .expiryDate(cardCreateDto.expiryDate())
                .cardHolder(cardCreateDto.cardHolder())
                .balance(cardCreateDto.balance())
                .status(cardValidator.isCardExpired(cardCreateDto.expiryDate()) ? CardStatus.EXPIRED : CardStatus.ACTIVE)
                .user(userService.getUserById(userId))
                .build();

        cardRepository.save(card);
        log.info("New card (details: {}, {}, {}) created by user with id: {}  ",
                cardUtils.maskCardNumber(cardCreateDto.cardNumber()),
                cardCreateDto.expiryDate(),
                cardCreateDto.cardHolder(),
                userId);
    }

    public Page<CardDto> getUserCards(UUID userId, Pageable pageable) {
        return cardRepository.findAllByUser(userService.getUserById(userId), pageable).map(cardMapper::toCardDto);
    }

    @Transactional
    public void makeMoneyTransfer(String fromCardNumber, String toCardNumber, UUID userId, double amount) {
        Card from = cardRepository.findByCardNumberAndUser(fromCardNumber,
                userService.getUserById(userId));
        Card to = cardRepository.findByCardNumberAndUser(toCardNumber,
                userService.getUserById(userId));

        if (from.equals(to))
            throw new RuntimeException("From and To are the same");

        if(from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE)
            throw new InactiveStatusException("Both cards must be active");

        if (from.getBalance() < amount)
            throw new InvalidDepositException("Not enough money to transfer from " + fromCardNumber);

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        cardRepository.saveAll(List.of(from, to));

        log.info("Money transfer from card {} to card {} done for user {} with amount of {}",
                fromCardNumber, toCardNumber, userId, amount);
    }

}
