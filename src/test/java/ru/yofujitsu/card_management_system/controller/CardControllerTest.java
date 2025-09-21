package ru.yofujitsu.card_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yofujitsu.card_management_system.config.SecurityConfig;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.card.MoneyTransferDto;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;
import ru.yofujitsu.card_management_system.exception.MoneyTransferException;
import ru.yofujitsu.card_management_system.service.CardService;
import ru.yofujitsu.card_management_system.service.CustomUserDetailsService;
import ru.yofujitsu.card_management_system.service.UserService;
import ru.yofujitsu.card_management_system.util.JwtUtil;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserCards_ShouldReturnPageOfCardDto() throws Exception {
        String username = "testuser";
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardDto> cardPage = new PageImpl<>(List.of(CardDto.builder().build()));
        when(cardService.getUserCards(anyString(), any(Pageable.class))).thenReturn(cardPage);

        mockMvc.perform(get("/api/v1/cards")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(cardService, times(1)).getUserCards(username, pageable);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testMakeMoneyTransfer_ShouldReturnOk() throws Exception {
        String username = "testuser";
        MoneyTransferDto transferDto = new MoneyTransferDto(UUID.randomUUID(), UUID.randomUUID(), 50.0);
        doNothing().when(cardService).makeMoneyTransfer(anyString(), any(MoneyTransferDto.class));

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDto)))
                .andExpect(status().isOk());

        verify(cardService, times(1)).makeMoneyTransfer(username, transferDto);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testMakeMoneyTransfer_ShouldReturnBadRequest_WhenTransferFails() throws Exception {
        MoneyTransferDto transferDto = new MoneyTransferDto(UUID.randomUUID(), UUID.randomUUID(), 5000.0);
        doThrow(new MoneyTransferException("Not enough money")).when(cardService).makeMoneyTransfer(anyString(), any(MoneyTransferDto.class));

        mockMvc.perform(post("/api/v1/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Not enough money"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSearchCards_ShouldReturnPageOfCards() throws Exception {
        Page<CardDto> cardPage = new PageImpl<>(List.of(CardDto.builder().build()));
        when(userService.getUserByUsername(anyString())).thenReturn(new ru.yofujitsu.card_management_system.entity.user.User());
        when(cardService.searchCards(anyString(), anyString(), anyString(), any(CardStatus.class), any(Pageable.class))).thenReturn(cardPage);

        mockMvc.perform(get("/api/v1/cards/search")
                        .param("cardNumber", "123").param("status", "ACTIVE"))
                .andExpect(status().isOk());
    }
}
