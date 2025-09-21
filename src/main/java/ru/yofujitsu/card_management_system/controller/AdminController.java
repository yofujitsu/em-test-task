package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.card_block_request.CardBlockRequest;
import ru.yofujitsu.card_management_system.service.CardService;
import ru.yofujitsu.card_management_system.service.UserService;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "Endpoints available only to admins")
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping("/cards")
    @Operation(summary = "Get all cards")
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users")
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/block-requests")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all card block requests from users")
    public Page<CardBlockRequest> getAllCardBlockRequests(Pageable pageable) {
        return cardService.getAllCardBlockRequests(pageable);
    }

    @PostMapping("/create-card")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new card for user")
    public void createCard(@RequestParam String username,
                           @RequestBody CardCreateDto cardDto) {
        cardService.addNewCard(cardDto, username);
    }

    @PostMapping("/block-card")
    @Operation(summary = "Handle card block request from user")
    public void blockCard(@RequestParam UUID requestId) {
        cardService.blockCard(requestId);
    }

    @PostMapping("/unblock-card")
    @Operation(summary = "Unblock user's card")
    public void unblockCard(@RequestParam UUID cardId) {
        cardService.unblockCard(cardId);
    }

    @DeleteMapping("/delete-card")
    @Operation(summary = "Delete user's card")
    public void deleteCard(@RequestParam UUID cardId) {
        cardService.deleteCard(cardId);
    }

    @DeleteMapping("/remove-user")
    @Operation(summary = "Delete user from system")
    public void removeUser(@RequestParam String username) {
        userService.removeUser(username);
    }

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get current user's data (for admins)")
    public UserDto getCurrentUser(Principal principal) {
        return userService.getUserDtoByUsername(principal.getName());
    }
}