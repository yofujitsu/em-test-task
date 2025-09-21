package ru.yofujitsu.card_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.card_management_system.config.SecurityConfig;
import ru.yofujitsu.card_management_system.dto.auth.ResponseTokenDto;
import ru.yofujitsu.card_management_system.dto.auth.SignInRequestDto;
import ru.yofujitsu.card_management_system.dto.auth.SignUpRequestDto;
import ru.yofujitsu.card_management_system.service.AuthService;
import ru.yofujitsu.card_management_system.service.CustomUserDetailsService;
import ru.yofujitsu.card_management_system.util.JwtUtil;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSignIn_ShouldReturnToken_WhenValidCredentials() throws Exception {
        SignInRequestDto signInRequestDto = new SignInRequestDto("user", "password");
        ResponseTokenDto responseTokenDto = new ResponseTokenDto("mock_jwt_token");
        when(authService.authenticate(signInRequestDto.username(), signInRequestDto.password()))
                .thenReturn(responseTokenDto);

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseTokenDto.token()));
    }

    @Test
    void testSignIn_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        SignInRequestDto signInRequestDto = new SignInRequestDto("wrong_user", "wrong_password");
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignUp_ShouldCreateAndReturnToken_WhenValidSignUpRequest() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("new_user", "email@example.com", "password");
        ResponseTokenDto responseTokenDto = new ResponseTokenDto("new_user_token");

        doNothing().when(authService).handleSignUp(any(SignUpRequestDto.class));
        when(authService.authenticate(anyString(), anyString())).thenReturn(responseTokenDto);

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(responseTokenDto.token()));

        verify(authService, times(1)).handleSignUp(signUpRequestDto);
        verify(authService, times(1)).authenticate(signUpRequestDto.username(), signUpRequestDto.password());
    }

    @Test
    void testSignUp_ShouldReturnBadRequest_WhenSignUpRequestThrowsValidationException() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("invalid_user", "email@example.com", "password");
        doThrow(new ValidationException("Validation failed")).when(authService).handleSignUp(any(SignUpRequestDto.class));

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(authService, times(1)).handleSignUp(signUpRequestDto);
        verify(authService, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testSignUp_ShouldReturnUnauthorized_WhenSignUpRequestThrowsBadCredentialsException() throws Exception {
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto("existing_user", "email@example.com", "password");
        doThrow(new BadCredentialsException("Username existing_user is already in use")).when(authService).handleSignUp(any(SignUpRequestDto.class));

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Username existing_user is already in use"));

        verify(authService, times(1)).handleSignUp(signUpRequestDto);
        verify(authService, never()).authenticate(anyString(), anyString());
    }
}
