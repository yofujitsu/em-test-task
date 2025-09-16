package ru.yofujitsu.card_management_system.entity.card;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yofujitsu.card_management_system.entity.user.User;

import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false)
    private String cardNumber;
    @Column(nullable = false)
    private String cardHolder;
    @Column(nullable = false)
    private String expiryDate;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    private double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
