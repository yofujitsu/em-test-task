package ru.yofujitsu.card_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yofujitsu.card_management_system.dto.auth.ResponseTokenDto;
import ru.yofujitsu.card_management_system.dto.auth.SignUpRequestDto;
import ru.yofujitsu.card_management_system.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ResponseTokenDto authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        return new ResponseTokenDto(jwt);
    }

    public void handleSignUp(SignUpRequestDto signUpRequestDto) {
        if (userService.existsByUsername(signUpRequestDto.username()))
            throw new BadCredentialsException("Username is already in use");

        if (userService.existsByEmail(signUpRequestDto.email()))
            throw new BadCredentialsException("Email is already in use");

        userService.createUser(signUpRequestDto.username(), signUpRequestDto.email(), passwordEncoder.encode(signUpRequestDto.password()));
    }
}