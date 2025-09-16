package ru.yofujitsu.card_management_system.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.service.CardService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCard(@PathVariable UUID userId,
                           @RequestBody @Valid CardCreateDto cardCreateDto) {
        cardService.addNewCard(cardCreateDto, userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardDto> getUserCards(@PathVariable UUID userId, Pageable pageable) {
        return cardService.getUserCards(userId, pageable);
    }

    @PostMapping("/{userId}/{from}/{to}")
    @ResponseStatus(HttpStatus.OK)
    public void makeMoneyTransfer(@PathVariable UUID userId,
                                  @PathVariable String from,
                                  @PathVariable String to,
                                  @RequestParam @Positive double amount) {
        cardService.makeMoneyTransfer(from, to, userId, amount);
    }
}
