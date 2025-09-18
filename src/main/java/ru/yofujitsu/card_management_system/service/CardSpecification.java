package ru.yofujitsu.card_management_system.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.card.CardStatus;

@Component
public class CardSpecification {

    public static Specification<Card> hasUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get("user").get("username"), username);
    }

    public static Specification<Card> hasCardNumberLike(String cardNumber) {
        return (root, query, cb) ->
                cardNumber == null ? null : cb.like(cb.lower(root.get("cardNumber")), "%" + cardNumber.toLowerCase() + "%");
    }

    public static Specification<Card> hasCardHolderLike(String cardHolder) {
        return (root, query, cb) ->
                cardHolder == null ? null : cb.like(cb.lower(root.get("cardHolder")), "%" + cardHolder.toLowerCase() + "%");
    }

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("cardStatus"), status);
    }
}
