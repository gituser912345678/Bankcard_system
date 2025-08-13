package org.example.bankcard_systems.service;

import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardAdminService {

    Page<Card> getAllCards(Pageable pageable);

    Page<Card> searchCardsByNumber(String numberMasked, Pageable pageable);

    Page<Card> getCardsByStatus(CardStatus status, Pageable pageable);

    Card getCardById(Long cardId);

    Page<Card> getUserCards(Long userId, Pageable pageable);

    Card createCardForUser(Long userId, Card card);

    Card blockCard(Long cardId, String reason);

    Card activateCard(Long cardId);

    void deleteCard(Long cardId);

}
