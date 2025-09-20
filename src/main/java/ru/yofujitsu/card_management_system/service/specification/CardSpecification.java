package ru.yofujitsu.card_management_system.service.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;

@Component
public class CardSpecification {

    /**
     * Метод для поиска карт пользователя по юзернейму
     *
     * @param username введенный юзернейм пользователя
     * @return объект спецификации с совпадениями по запросу
     */
    public static Specification<Card> hasUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get("user").get("username"), username);
    }

    /**
     * Метод для поиска карт пользователя по номеру карты
     *
     * @param cardNumber введенный номер карты
     * @return объект спецификации с совпадениями по запросу
     */
    public static Specification<Card> hasCardNumberLike(String cardNumber) {
        return (root, query, cb) ->
                cardNumber == null ? null : cb.like(cb.lower(root.get("cardNumber")), "%" + cardNumber.toLowerCase() + "%");
    }

    /**
     * Метод для поиска карт пользователя по владельцу
     *
     * @param cardHolder введенный владелец карты
     * @return объект спецификации с совпадениями по запросу
     */
    public static Specification<Card> hasCardHolderLike(String cardHolder) {
        return (root, query, cb) ->
                cardHolder == null ? null : cb.like(cb.lower(root.get("cardHolder")), "%" + cardHolder.toLowerCase() + "%");
    }

    /**
     * Метод для поиска карт пользователя по статусу
     *
     * @param status введенный статус карты
     * @return объект спецификации с совпадениями по запросу
     */
    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
