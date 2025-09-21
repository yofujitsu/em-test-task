package ru.yofujitsu.card_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yofujitsu.card_management_system.dto.auth.ResponseTokenDto;
import ru.yofujitsu.card_management_system.dto.auth.SignInRequestDto;
import ru.yofujitsu.card_management_system.dto.auth.SignUpRequestDto;
import ru.yofujitsu.card_management_system.service.AuthService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authorization Controller", description = "Endpoints for user's authorization")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Log into account")
    public ResponseEntity<ResponseTokenDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
        return ResponseEntity.ok(authService.authenticate(signInRequestDto.username(), signInRequestDto.password()));
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Reqister new account")
    public ResponseEntity<ResponseTokenDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        authService.handleSignUp(signUpRequestDto);
        return ResponseEntity.ok(authService.authenticate(signUpRequestDto.username(), signUpRequestDto.password()));
    }

}
