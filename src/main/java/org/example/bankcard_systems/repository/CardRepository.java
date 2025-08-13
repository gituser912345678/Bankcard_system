package org.example.bankcard_systems.repository;

import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByUserId(Long userId, Pageable pageable);
    Page<Card> findByUserIdAndCardNumberContaining(Long userId, String cardNumber, Pageable pageable);

    Page<Card> findByCardNumberContaining(String numberPart, Pageable pageable);
    Page<Card> findByStatus(CardStatus status, Pageable pageable);

}
