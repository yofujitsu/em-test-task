package ru.yofujitsu.card_management_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.yofujitsu.card_management_system.entity.card.Card;
import ru.yofujitsu.card_management_system.entity.user.User;

import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    Page<Card> findAllByUser(User user, Pageable pageable);

    Card findByCardNumberAndUser(String cardNumber, User user);
}
