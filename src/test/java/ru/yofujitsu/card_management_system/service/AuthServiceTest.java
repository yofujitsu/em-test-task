package ru.yofujitsu.card_management_system.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.yofujitsu.card_management_system.dto.auth.ResponseTokenDto;
import ru.yofujitsu.card_management_system.dto.auth.SignUpRequestDto;
import ru.yofujitsu.card_management_system.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        String username = "test_user";
        String password = "password";
        String token = "mock_jwt_token";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(authentication)).thenReturn(token);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        ResponseTokenDto result = authService.authenticate(username, password);

        assertNotNull(result);
        assertEquals(token, result.token());
        verify(securityContext, times(1)).setAuthentication(authentication);
    }

    @Test
    void testHandleSignUp_ShouldCreateUser_WhenCredentialsAreUnique() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("new_user", "new@example.com", "password");
        when(userService.existsByUsername(signUpRequestDto.username())).thenReturn(false);
        when(userService.existsByEmail(signUpRequestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(signUpRequestDto.password())).thenReturn("encoded_password");

        authService.handleSignUp(signUpRequestDto);

        verify(userService, times(1)).createUser(
                signUpRequestDto.username(),
                signUpRequestDto.email(),
                "encoded_password"
        );
    }

    @Test
    void testHandleSignUp_ShouldThrowException_WhenUsernameExists() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("existing_user", "new@example.com", "password");
        when(userService.existsByUsername(signUpRequestDto.username())).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> authService.handleSignUp(signUpRequestDto));
        verify(userService, never()).createUser(any(), any(), any());
    }

    @Test
    void testHandleSignUp_ShouldThrowException_WhenEmailExists() {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("new_user", "existing@example.com", "password");
        when(userService.existsByUsername(signUpRequestDto.username())).thenReturn(false);
        when(userService.existsByEmail(signUpRequestDto.email())).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> authService.handleSignUp(signUpRequestDto));
        verify(userService, never()).createUser(any(), any(), any());
    }
}
