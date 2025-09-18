package ru.yofujitsu.card_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yofujitsu.card_management_system.entity.card_block_request.CardBlockRequest;

import java.util.UUID;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, UUID> {
}
