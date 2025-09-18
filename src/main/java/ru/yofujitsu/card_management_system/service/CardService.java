package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yofujitsu.card_management_system.dto.card.CardBlockRequestDto;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.card.MoneyTransferDto;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;
import ru.yofujitsu.card_management_system.entity.card_block_request.CardBlockRequest;
import ru.yofujitsu.card_management_system.entity.card_block_request.CardBlockRequestStatus;
import ru.yofujitsu.card_management_system.exception.CardBlockRequestNotFoundException;
import ru.yofujitsu.card_management_system.exception.CardNotFoundException;
import ru.yofujitsu.card_management_system.exception.InactiveStatusException;
import ru.yofujitsu.card_management_system.exception.InvalidTransferAmountException;
import ru.yofujitsu.card_management_system.mapper.CardMapper;
import ru.yofujitsu.card_management_system.repository.CardBlockRequestRepository;
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
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final UserService userService;

    public void addNewCard(CardCreateDto cardCreateDto, String username) {
        cardValidator.validateCard(cardCreateDto.cardNumber(), cardCreateDto.expiryDate());

        Card card = Card.builder()
                .cardNumber(cardCreateDto.cardNumber())
                .expiryDate(cardCreateDto.expiryDate())
                .cardHolder(cardCreateDto.cardHolder())
                .balance(cardCreateDto.balance())
                .status(cardValidator.isCardExpired(cardCreateDto.expiryDate()) ? CardStatus.EXPIRED : CardStatus.ACTIVE)
                .user(userService.getUserByUsername(username))
                .build();

        cardRepository.save(card);
        log.info("New card (details: {}, {}, {}) created by user with id: {}",
                cardUtils.maskCardNumber(cardCreateDto.cardNumber()),
                cardCreateDto.expiryDate(),
                cardCreateDto.cardHolder(),
                username);
    }

    public Page<CardDto> getUserCards(String username, Pageable pageable) {
        return cardRepository.findAllByUser(userService.getUserByUsername(username), pageable).map(cardMapper::toCardDto);
    }

    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(cardMapper::toCardDto);
    }

    public Page<CardDto> searchCards(String username, String cardNumber, String cardHolder, CardStatus status, Pageable pageable) {
        Specification<Card> spec = CardSpecification.hasUsername(username)
                .and(CardSpecification.hasCardNumberLike(cardNumber))
                .and(CardSpecification.hasCardHolderLike(cardHolder))
                .and(CardSpecification.hasStatus(status));

        return cardRepository.findAll(spec, pageable).map(cardMapper::toCardDto);
    }

    public void deleteCard(UUID cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException("Card not found with id %s".formatted(cardId)));
        cardRepository.deleteById(cardId);
        log.info("Deleted card for user: {} with number: {})",
                card.getUser().getUsername(), card.getCardNumber());
    }

    @Transactional
    public void makeMoneyTransfer(String username, MoneyTransferDto moneyTransferDto) {
        Card from = cardRepository.findByCardNumberAndUser(moneyTransferDto.fromCardNumber(),
                userService.getUserByUsername(username));
        Card to = cardRepository.findByCardNumberAndUser(moneyTransferDto.toCardNumber(),
                userService.getUserByUsername(username));

        if (from.equals(to))
            throw new RuntimeException("From and To are the same");

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE)
            throw new InactiveStatusException("Both cards must be active");

        if (from.getBalance() < moneyTransferDto.amount())
            throw new InvalidTransferAmountException("Not enough money to transfer from card %s"
                    .formatted(cardUtils.maskCardNumber(moneyTransferDto.fromCardNumber())));

        from.setBalance(from.getBalance() - moneyTransferDto.amount());
        to.setBalance(to.getBalance() + moneyTransferDto.amount());
        cardRepository.saveAll(List.of(from, to));

        log.info("Money transfer from card: {} to card: {} done by user with username: {} with amount of {}",
                cardUtils.maskCardNumber(moneyTransferDto.fromCardNumber()),
                cardUtils.maskCardNumber(moneyTransferDto.toCardNumber()),
                username, moneyTransferDto.amount());
    }

    public void createCardBlockRequest(CardBlockRequestDto cardBlockRequestDto) {
        CardBlockRequest cardBlockRequest = new CardBlockRequest();
        cardBlockRequest.setCardId(cardBlockRequestDto.cardId());
        cardBlockRequest.setUsername(cardBlockRequestDto.username());

        Card card = cardRepository.findById(cardBlockRequestDto.cardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found with id %s"
                        .formatted(cardBlockRequestDto.cardId())));

        cardBlockRequestRepository.save(cardBlockRequest);
        log.info("Card block request created for card: {} by user: {}",
                cardUtils.maskCardNumber(card.getCardNumber()), cardBlockRequestDto.username());
    }

    @Transactional
    public void blockCard(UUID requestId) {
        CardBlockRequest cardBlockRequest = cardBlockRequestRepository.findById(requestId)
                .orElseThrow(() -> new CardBlockRequestNotFoundException(
                        "Card block request with id %s not found".formatted(requestId)
                ));
        Card card = cardRepository.findById(cardBlockRequest.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card not found with id %s"
                        .formatted(cardBlockRequest.getCardId())));


        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        cardBlockRequest.setStatus(CardBlockRequestStatus.RESOLVED);
        cardBlockRequestRepository.save(cardBlockRequest);

        log.info("User's {} card {} blocked",
                cardBlockRequest.getUsername(), cardUtils.maskCardNumber(card.getCardNumber()));
    }

    public void unblockCard(String cardNumber, String username) {
        Card card = cardRepository.findByCardNumberAndUser(cardNumber, userService.getUserByUsername(username));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        log.info("User's {} card {} activated",
                username, cardUtils.maskCardNumber(cardNumber));
    }
}