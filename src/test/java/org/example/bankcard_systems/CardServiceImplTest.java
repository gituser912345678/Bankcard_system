package org.example.bankcard_systems;

import org.example.bankcard_systems.dto.auth.CardBalanceResponse;
import org.example.bankcard_systems.exception.AccessDeniedException;
import org.example.bankcard_systems.exception.NotFoundException;
import org.example.bankcard_systems.model.*;
import org.example.bankcard_systems.repository.CardBlockRequestRepository;
import org.example.bankcard_systems.repository.CardRepository;
import org.example.bankcard_systems.repository.UserRepository;
import org.example.bankcard_systems.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardBlockRequestRepository blockRequestRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User testUser;
    private Card testCard;
    private final Long userId = 1L;
    private final Long cardId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");

        testCard = new Card();
        testCard.setId(cardId);
        testCard.setCardNumber("1234567890123456");
        testCard.setNumberMasked("**** **** **** 3456");
        testCard.setBalance(BigDecimal.valueOf(1000));
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setUser(testUser);
    }

    @Test
    void createCard_ShouldCreateNewCard() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.createCard(userId);

        assertNotNull(result);
        assertEquals(testCard, result);
        verify(userRepository).findById(userId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            cardService.createCard(userId);
        });
    }

    @Test
    void getUserCards_WithSearchTerm_ShouldReturnFilteredCards() {
        String searchTerm = "3456";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));

        when(cardRepository.findByUserIdAndCardNumberContaining(userId, searchTerm, pageable))
                .thenReturn(cardPage);

        Page<Card> result = cardService.getUserCards(userId, searchTerm, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUserIdAndCardNumberContaining(userId, searchTerm, pageable);
    }

    @Test
    void getUserCards_WithoutSearchTerm_ShouldReturnAllCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));

        when(cardRepository.findByUserId(userId, pageable)).thenReturn(cardPage);

        Page<Card> result = cardService.getUserCards(userId, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUserId(userId, pageable);
    }

    @Test
    void blockCard_ShouldBlockCardAndSaveRequest() {
        String message = "Lost card";
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        when(blockRequestRepository.save(any(CardBlockRequest.class))).thenReturn(new CardBlockRequest());

        Card result = cardService.blockCard(userId, cardId, message);

        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository).findById(cardId);
        verify(userRepository).findById(userId);
        verify(cardRepository).save(testCard);
        verify(blockRequestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void blockCard_WhenCardNotFound_ShouldThrowException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cardService.blockCard(userId, cardId, "Test");
        });
    }

    @Test
    void blockCard_WhenUserNotOwner_ShouldThrowException() {
        Long otherUserId = 2L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(userRepository.findById(otherUserId)).thenReturn(Optional.of(new User()));

        assertThrows(AccessDeniedException.class, () -> {
            cardService.blockCard(otherUserId, cardId, "Test");
        });
    }

    @Test
    void getCardBalance_ShouldReturnBalance() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        CardBalanceResponse result = cardService.getCardBalance(userId, cardId);

        assertNotNull(result);
        assertEquals(testCard.getBalance(), result.getBalance());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardBalance_WhenCardNotFound_ShouldThrowException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cardService.getCardBalance(userId, cardId);
        });
    }

    @Test
    void getCardBalance_WhenUserNotOwner_ShouldThrowException() {
        Long otherUserId = 2L;
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        assertThrows(AccessDeniedException.class, () -> {
            cardService.getCardBalance(otherUserId, cardId);
        });
    }
}