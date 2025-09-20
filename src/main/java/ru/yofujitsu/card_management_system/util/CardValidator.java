package ru.yofujitsu.card_management_system.util;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class CardValidator {

    private static final String EXPIRY_DATE_REGEXP = "^(0[1-9]|1[0-2])/\\d{2}$";
    private static final String CARD_NUMBER_REGEXP = "^[0-9]{16}$";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    /**
     * Общий метод валидации номера и срока действия карты
     *
     * @param cardNumber введенный номер карты
     * @param expiryDate введенный срок действия карты
     */
    public void validateCard(String cardNumber, String expiryDate) {
        validateCardNumber(cardNumber);
        validateExpireDate(expiryDate);
    }

    /**
     * Проверка срока действия карты
     *
     * @param expiryDate введенный срок действия карты
     * @return true/false - карта истекла/не истекла
     */
    public boolean isCardExpired(String expiryDate) {
        YearMonth yearMonth = YearMonth.parse(expiryDate, FORMATTER);
        YearMonth now = YearMonth.now();

        return yearMonth.isBefore(now);
    }

    /**
     * Метод валидации номера карты
     *
     * @param cardNumber введенный номер карты
     */
    private void validateCardNumber(String cardNumber) {
        if (cardNumber.isEmpty())
            throw new ValidationException("Card number must be entered.");
        if (!cardNumber.matches(CARD_NUMBER_REGEXP))
            throw new ValidationException("Card number must consist of 16 digits.");
        if (!checkCardNumberByLuhn(cardNumber))
            throw new ValidationException("Non-existent card number has been entered.");
    }

    /**
     * Проверка номера карты на соответствие алгоритму Луна
     *
     * @param cardNumber номер карты
     * @return true/false - соответствует/не соответствует
     */
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

    /**
     * Метод валидации срока действия карты
     *
     * @param expiryDate введенный срок действия карты
     */
    private void validateExpireDate(String expiryDate) {
        if (expiryDate.isEmpty())
            throw new ValidationException("Expiration date is required to be entered.");
        if (!expiryDate.matches(EXPIRY_DATE_REGEXP))
            throw new ValidationException("Expiration date must be in MM/yy format.");
    }
}