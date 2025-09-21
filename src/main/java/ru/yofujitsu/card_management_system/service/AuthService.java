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

    /**
     * Метод аутентификации пользователя
     *
     * @param username юзернейм пользователя
     * @param password зашифрованный пароль
     * @return сформированный jwt-токен в виде строки
     */
    public ResponseTokenDto authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        return new ResponseTokenDto(jwt);
    }

    /**
     * Метод, обрабатывающий регистрацию пользователя
     * Проводит первичную валидацию уникальных полей
     * Затем создает пользователя в БД
     *
     * @param signUpRequestDto дто объект с регистрационными данными пользователя {@link SignUpRequestDto}
     */
    public void handleSignUp(SignUpRequestDto signUpRequestDto) {
        if (userService.existsByUsername(signUpRequestDto.username()))
            throw new BadCredentialsException("Username %s is already in use".formatted(signUpRequestDto.username()));

        if (userService.existsByEmail(signUpRequestDto.email()))
            throw new BadCredentialsException("Email %s is already in use".formatted(signUpRequestDto.email()));

        userService.createUser(signUpRequestDto.username(), signUpRequestDto.email(), passwordEncoder.encode(signUpRequestDto.password()));
    }
}