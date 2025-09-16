package ru.yofujitsu.card_management_system.util;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.exception.InvalidCardNumberException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CardValidator {

    private static final String EXPIRY_DATE_REGEXP = "^(0[1-9]|1[0-2])/\\d{2}$";
    private static final String CARD_NUMBER_REGEXP = "^[0-9]{16}$";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    public void validateCard(String cardNumber, String expiryDate) {
        validateCardNumber(cardNumber);
        validateExpireDate(expiryDate);
    }

    public boolean isCardExpired(String expiryDate) {
        YearMonth yearMonth = YearMonth.parse(expiryDate, FORMATTER);
        YearMonth now = YearMonth.now();

        return yearMonth.isBefore(now);
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber.isEmpty())
            throw new InvalidCardNumberException("Номер карты обязателен к вводу.");
        if (!cardNumber.matches(CARD_NUMBER_REGEXP))
            throw new InvalidCardNumberException("Номер карты должен состоять из 16 цифр.");
        if (!checkCardNumberByLuhn(cardNumber))
            throw new InvalidCardNumberException("Введен несуществующий номер карты.");
    }

    private boolean checkCardNumberByLuhn(String cardNumber) {
        int sum = 0;
        boolean alt = false;
        for (int i = cardNumber.length() - 1; i >= 0; --i) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alt) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alt = !alt;
        }
        return (sum % 10 == 0);
    }

    private void validateExpireDate(String expiryDate) {
        if (expiryDate.isEmpty())
            throw new ValidationException("Срок истечения карты обязателен к вводу.");
        if (!expiryDate.matches(EXPIRY_DATE_REGEXP))
            throw new ValidationException("Срок истечения карты должен быть в формате ММ/гг.");
    }
}
