package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.card.CardBlockRequestDto;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.service.CardService;
import ru.yofujitsu.card_management_system.service.UserService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Controller")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping("/cards")
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/create-card")
    public void createCard(@RequestParam String username,
                           @RequestBody CardCreateDto cardDto) {
        cardService.addNewCard(cardDto, username);
    }

    @PostMapping("/block-card")
    public void blockCard(@RequestParam UUID requestId) {
        cardService.blockCard(requestId);
    }

    @PostMapping("/unblock-card")
    public void unblockCard(@RequestParam String username,
                            @RequestParam String cardNumber) {
        cardService.unblockCard(username, cardNumber);
    }

    @DeleteMapping("/delete-card")
    public void deleteCard(@RequestParam UUID cardId) {
        cardService.deleteCard(cardId);
    }

    @DeleteMapping("/remove-user")
    public void removeUser(@RequestParam String username) {
        userService.removeUser(username);
    }

}
