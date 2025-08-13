package org.example.bankcard_systems.service;

import org.example.bankcard_systems.dto.auth.CardBalanceResponse;
import org.example.bankcard_systems.dto.auth.TransferRequest;
import org.example.bankcard_systems.dto.auth.TransferResponse;
import org.example.bankcard_systems.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CardService {

    Card createCard(Long userId);

    Page<Card> getUserCards(Long userId, String searchTerm, Pageable pageable);

    Card blockCard(Long userId, Long cardId, String message);

    CardBalanceResponse getCardBalance(Long userId, Long cardId);

    TransferResponse transferBetweenOwnCards(Long userId, TransferRequest request);


}
