package ru.yofujitsu.card_management_system.service;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.yofujitsu.card_management_system.dto.card.CardCreateDto;
import ru.yofujitsu.card_management_system.dto.card.CardDto;
import ru.yofujitsu.card_management_system.dto.card.MoneyTransferDto;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;
import ru.yofujitsu.card_management_system.entity.user.User;
import ru.yofujitsu.card_management_system.exception.CardNotFoundException;
import ru.yofujitsu.card_management_system.exception.MoneyTransferException;
import ru.yofujitsu.card_management_system.mapper.CardMapper;
import ru.yofujitsu.card_management_system.repository.CardBlockRequestRepository;
import ru.yofujitsu.card_management_system.repository.CardRepository;
import ru.yofujitsu.card_management_system.util.CardUtils;
import ru.yofujitsu.card_management_system.util.CardValidator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardValidator cardValidator;

    @Mock
    private CardUtils cardUtils;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;
    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(UUID.randomUUID()).username("test_user").build();
        testCard = Card.builder().id(UUID.randomUUID()).cardNumber("1234567890123456").cardHolder("TEST CARDHOLDER").expiryDate("03/30").balance(100.0).status(CardStatus.ACTIVE).user(testUser).build();
        testCardDto = CardDto.builder().id(testCard.getId()).cardNumber(testCard.getCardNumber()).cardHolder(testCard.getCardHolder()).balance(testCard.getBalance()).build();
    }

    @Test
    void testAddNewCard_ShouldSaveCard_WhenDataIsValid() {
        String expiryDate = YearMonth.now().plusMonths(1).toString();
        CardCreateDto cardCreateDto = new CardCreateDto("1234123412341234", "TEST_HOLDER", expiryDate, 500.0);
        when(userService.getUserByUsername(anyString())).thenReturn(testUser);
        when(cardValidator.isCardExpired(anyString())).thenReturn(false);

        cardService.addNewCard(cardCreateDto, "test_user");

        verify(cardValidator, times(1)).validateCard(anyString(), anyString());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testAddNewCard_ShouldThrowValidationException_WhenCardNumberIsInvalid() {
        CardCreateDto cardCreateDto = new CardCreateDto("123", "TEST_HOLDER", "12/25", 500.0);
        doThrow(new ValidationException("Card number must consist of 16 digits."))
                .when(cardValidator).validateCard(anyString(), anyString());

        assertThrows(ValidationException.class, () -> cardService.addNewCard(cardCreateDto, "test_user"));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testAddNewCard_ShouldThrowValidationException_WhenExpiryDateIsInvalid() {
        CardCreateDto cardCreateDto = new CardCreateDto("1234123412341234", "TEST_HOLDER", "13/20", 500.0);
        doThrow(new ValidationException("Expiration date must be in MM/yy format."))
                .when(cardValidator).validateCard(anyString(), anyString());

        assertThrows(ValidationException.class, () -> cardService.addNewCard(cardCreateDto, "test_user"));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void testGetUserCards_ShouldReturnPageOfCardDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));

        when(userService.getUserByUsername("test_user")).thenReturn(testUser);
        when(cardRepository.findAllByUser(testUser, pageable)).thenReturn(cardPage);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(testCardDto);

        Page<CardDto> result = cardService.getUserCards("test_user", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCardDto.cardNumber(), result.getContent().get(0).cardNumber());
    }

    @Test
    void testMakeMoneyTransfer_ShouldSucceed_WhenValidTransfer() {
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();
        Card fromCard = Card.builder().id(fromCardId).user(testUser).cardNumber("from_card").balance(200.0).status(CardStatus.ACTIVE).build();
        Card toCard = Card.builder().id(toCardId).user(testUser).cardNumber("to_card").balance(100.0).status(CardStatus.ACTIVE).build();
        MoneyTransferDto transferDto = new MoneyTransferDto(fromCardId, toCardId, 50.0);

        when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        cardService.makeMoneyTransfer("test_user", transferDto);

        assertEquals(150.0, fromCard.getBalance());
        assertEquals(150.0, toCard.getBalance());
        verify(cardRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testMakeMoneyTransfer_ShouldThrowException_WhenSameCard() {
        UUID cardId = UUID.randomUUID();
        MoneyTransferDto transferDto = new MoneyTransferDto(cardId, cardId, 50.0);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        assertThrows(MoneyTransferException.class, () -> cardService.makeMoneyTransfer("test_user", transferDto));
    }

    @Test
    void testDeleteCard_ShouldDeleteCard_WhenCardExists() {
        when(cardRepository.findById(any(UUID.class))).thenReturn(Optional.of(testCard));

        cardService.deleteCard(testCard.getId());

        verify(cardRepository, times(1)).deleteById(testCard.getId());
    }

    @Test
    void testDeleteCard_ShouldThrowException_WhenCardDoesNotExist() {
        when(cardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(UUID.randomUUID()));
    }

    @Test
    void testUnblockCard_ShouldSetStatusToActive_WhenCardExists() {
        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(any(UUID.class))).thenReturn(Optional.of(testCard));

        cardService.unblockCard(testCard.getId());

        assertEquals(CardStatus.ACTIVE, testCard.getStatus());
        verify(cardRepository, times(1)).save(testCard);
    }
}
