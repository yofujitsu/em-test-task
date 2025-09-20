package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Cards Controller", description = "Endpoints for user's cards opportunities")
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all cards for current user")
    public Page<CardDto> getUserCards(Principal principal, Pageable pageable) {
        return cardService.getUserCards(principal.getName(), pageable);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Make money transfer from one card to another")
    public void makeMoneyTransfer(Principal principal,
                                  @RequestBody MoneyTransferDto moneyTransferDto) {
        cardService.makeMoneyTransfer(principal.getName(), moneyTransferDto);
    }

    @PostMapping("/block-card-request")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Make card block request (wait for admin's confirmation)")
    public void makeCardBlockRequest(@RequestParam UUID cardId, Principal principal) {

        CardBlockRequestDto cardBlockRequestDto = new CardBlockRequestDto(cardId, principal.getName());
        cardService.createCardBlockRequest(cardBlockRequestDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Search for cards by filters")
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