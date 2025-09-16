package ru.yofujitsu.card_management_system.util;

import org.springframework.stereotype.Component;

@Component
public class CardUtils {

    public String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
