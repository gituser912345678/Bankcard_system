package org.example.bankcard_systems.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.model.CardStatus;
import org.example.bankcard_systems.repository.CardRepository;
import org.example.bankcard_systems.repository.UserRepository;
import org.example.bankcard_systems.service.CardAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.bankcard_systems.model.User;

@Service
@RequiredArgsConstructor
public class CardAdminServiceImpl implements CardAdminService {


    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Card> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Override
    public Page<Card> searchCardsByNumber(String numberPart, Pageable pageable) {
        return cardRepository.findByCardNumberContaining(numberPart, pageable);
    }

    @Override
    public Page<Card> getCardsByStatus(CardStatus status, Pageable pageable) {
        return cardRepository.findByStatus(status, pageable);
    }

    @Override
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    @Override
    public Page<Card> getUserCards(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Card createCardForUser(Long userId, Card card) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);
        // Генерация номера карты и маскированного номера
        card.setNumberMasked("****" + card.getCardNumber().substring(card.getCardNumber().length() - 4));

        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public Card blockCard(Long cardId, String reason) {
        Card card = getCardById(cardId);
        card.setStatus(CardStatus.BLOCKED);
        // Можно добавить поле для хранения причины блокировки
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public Card activateCard(Long cardId) {
        Card card = getCardById(cardId);
        card.setStatus(CardStatus.ACTIVE);
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

}