package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.card.CardBlockRequestDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.card.MoneyTransferDto;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;
import ru.yofujitsu.card_management_system.service.CardService;
import ru.yofujitsu.card_management_system.service.UserService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User's cards Controller")
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<CardDto> getUserCards(Principal principal, Pageable pageable) {
        return cardService.getUserCards(principal.getName(), pageable);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void makeMoneyTransfer(Principal principal,
                                  @RequestBody MoneyTransferDto moneyTransferDto) {
        cardService.makeMoneyTransfer(principal.getName(), moneyTransferDto);
    }

    @PostMapping("/block-card-request")
    public void makeCardBlockRequest(@RequestParam UUID cardId, Principal principal) {

        CardBlockRequestDto cardBlockRequestDto = new CardBlockRequestDto(cardId, principal.getName());
        cardService.createCardBlockRequest(cardBlockRequestDto);
    }

    @GetMapping("/search")
    public Page<CardDto> searchCards(
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String cardHolder,
            @RequestParam(required = false) CardStatus status,
            Pageable pageable,
            Principal principal
    ) {
        String username = userService.getUserByUsername(principal.getName()).getUsername();
        return cardService.searchCards(username, cardNumber, cardHolder, status, pageable);
    }

}
