package ru.yofujitsu.card_management_system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.card_management_system.config.SecurityConfig;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.security.JwtAuthenticationFilter;
import ru.yofujitsu.card_management_system.service.CustomUserDetailsService;
import ru.yofujitsu.card_management_system.service.UserService;
import ru.yofujitsu.card_management_system.util.JwtUtil;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetCurrentUser_ShouldReturnUserDto_WhenAuthenticated() throws Exception {
        String username = "testuser";
        UserDto userDto = new UserDto(UUID.randomUUID(), "test@example.com", username, null, null);
        when(userService.getUserDtoByUsername(username)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUserDtoByUsername(username);
    }

    @Test
    void testGetCurrentUser_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getUserDtoByUsername(anyString());
    }
}
