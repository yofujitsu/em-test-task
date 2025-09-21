package ru.yofujitsu.card_management_system.entity.card_block_request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "card_block_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardBlockRequest {

    @Id
    @UuidGenerator
    private UUID id;
    @Column(nullable = false)
    private UUID cardId;
    @Column(nullable = false)
    private String username;
    @CreationTimestamp
    private Instant datetime;
    @Enumerated(EnumType.STRING)
    private CardBlockRequestStatus status = CardBlockRequestStatus.PROGRESS;
}
