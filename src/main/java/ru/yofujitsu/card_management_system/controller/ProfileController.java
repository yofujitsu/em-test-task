package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.service.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Profile Controller")
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getCurrentUser(Principal principal) {
        return userService.getUserDtoByUsername(principal.getName());
    }

}
