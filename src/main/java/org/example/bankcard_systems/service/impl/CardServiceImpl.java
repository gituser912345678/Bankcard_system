package org.example.bankcard_systems.service.impl;

import org.example.bankcard_systems.dto.auth.CardBalanceResponse;
import org.example.bankcard_systems.dto.auth.TransferRequest;
import org.example.bankcard_systems.dto.auth.TransferResponse;
import org.example.bankcard_systems.exception.AccessDeniedException;
import org.example.bankcard_systems.exception.NotFoundException;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.model.CardBlockRequest;
import org.example.bankcard_systems.model.CardStatus;
import org.example.bankcard_systems.model.User;
import org.example.bankcard_systems.repository.CardBlockRequestRepository;
import org.example.bankcard_systems.repository.CardRepository;
import org.example.bankcard_systems.repository.UserRepository;
import org.example.bankcard_systems.security.JwtTokenProvider;
import org.example.bankcard_systems.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    private final CardBlockRequestRepository blockRequestRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public Card createCard(Long userId) {
        // 1. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 2. Создаем новую карту
        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setNumberMasked(maskCardNumber(card.getCardNumber()));
        card.setExpiryDate(generateExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        // 3. Сохраняем карту в базу данных
        return cardRepository.save(card);
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(12);
    }

    private LocalDate generateExpiryDate() {
        return LocalDate.now().plusYears(3);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Card> getUserCards(Long userId, String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // Поиск по номеру карты (можно добавить другие поля)
            return cardRepository.findByUserIdAndCardNumberContaining(userId, searchTerm, pageable);
        } else {
            // Все карты пользователя
            return cardRepository.findByUserId(userId, pageable);
        }
    }

    @Override
    @Transactional
    public Card blockCard(Long userId, Long cardId, String message) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to block this card");
        }

        // Блокируем карту
        card.setStatus(CardStatus.BLOCKED);
        Card blockedCard = cardRepository.save(card);

        // Сохраняем запрос в историю
        CardBlockRequest request = new CardBlockRequest();
        request.setCard(blockedCard);
        request.setUser(user);
        request.setMessage(message);
        blockRequestRepository.save(request);

        return blockedCard;
    }

    @Override
    @Transactional(readOnly = true)
    public CardBalanceResponse getCardBalance(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to view this card's balance");
        }

        return new CardBalanceResponse(
                card.getId(),
                card.getNumberMasked(),
                card.getBalance()
        );
    }

    @Override
    @Transactional
    public TransferResponse transferBetweenOwnCards(Long userId, TransferRequest request) {
        // 1. Проверяем существование карт
        Card fromCard = cardRepository.findById(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Source card not found"));

        Card toCard = cardRepository.findById(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("Target card not found"));

        // 2. Проверяем принадлежность карт пользователю
        if (!fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Both cards must belong to the user");
        }

        // 3. Проверяем достаточность средств
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds on source card");
        }

        // 4. Проверяем активность карт
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Both cards must be active");
        }

        // 5. Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        // 6. Сохраняем карты
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return new TransferResponse(
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount(),
                "COMPLETED"
        );
    }

}