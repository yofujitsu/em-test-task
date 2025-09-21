package ru.yofujitsu.card_management_system.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.card_management_system.config.SecurityConfig;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.user.UserDto;
import ru.yofujitsu.card_management_system.entity.card_block_request.CardBlockRequest;
import ru.yofujitsu.card_management_system.service.CardService;
import ru.yofujitsu.card_management_system.service.CustomUserDetailsService;
import ru.yofujitsu.card_management_system.service.UserService;
import ru.yofujitsu.card_management_system.util.JwtUtil;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_shouldReturnPageOfCards() throws Exception {
        CardDto cardDto = CardDto.builder()
                .id(UUID.randomUUID())
                .cardNumber("1234")
                .cardHolder("User")
                .expiryDate("12/25")
                .balance(100)
                .build();

        Mockito.when(cardService.getAllCards(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(cardDto)));

        mockMvc.perform(get("/api/v1/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber").value("1234"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnPageOfUsers() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@mail.com")
                .build();

        Mockito.when(userService.getAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userDto)));

        mockMvc.perform(get("/api/v1/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCardBlockRequests_shouldReturnPage() throws Exception {
        CardBlockRequest request = new CardBlockRequest();
        Mockito.when(cardService.getAllCardBlockRequests(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(request)));

        mockMvc.perform(get("/api/v1/admin/block-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldReturnCreated() throws Exception {

        mockMvc.perform(post("/api/v1/admin/create-card")
                        .param("username", "testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "cardNumber":"1234123412341234",
                                   "cardHolder":"User",
                                   "expiryDate":"12/25",
                                   "balance":100
                                }
                                """))
                .andExpect(status().isCreated());

        Mockito.verify(cardService).addNewCard(any(CardCreateDto.class), eq("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_shouldCallService() throws Exception {
        UUID requestId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/admin/block-card")
                        .param("requestId", requestId.toString()))
                .andExpect(status().isOk());

        Mockito.verify(cardService).blockCard(eq(requestId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unblockCard_shouldCallService() throws Exception {
        UUID cardId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/admin/unblock-card")
                        .param("cardId", cardId.toString()))
                .andExpect(status().isOk());

        Mockito.verify(cardService).unblockCard(eq(cardId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_shouldCallService() throws Exception {
        UUID cardId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/admin/delete-card")
                        .param("cardId", cardId.toString()))
                .andExpect(status().isOk());

        Mockito.verify(cardService).deleteCard(eq(cardId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeUser_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/remove-user")
                        .param("username", "testuser"))
                .andExpect(status().isOk());

        Mockito.verify(userService).removeUser("testuser");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllUsers_shouldReturnForbiddenForUserRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/cards"))
                .andExpect(status().isForbidden());
    }
}