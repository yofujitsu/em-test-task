package ru.yofujitsu.card_management_system.exception;

public class CardBlockRequestNotFoundException extends RuntimeException {
    public CardBlockRequestNotFoundException(String message) {
        super(message);
    }
}
