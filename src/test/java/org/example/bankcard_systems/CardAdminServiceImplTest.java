package org.example.bankcard_systems;

import jakarta.persistence.EntityNotFoundException;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.model.CardStatus;
import org.example.bankcard_systems.model.User;
import org.example.bankcard_systems.repository.CardRepository;
import org.example.bankcard_systems.repository.UserRepository;
import org.example.bankcard_systems.service.impl.CardAdminServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardAdminServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardAdminServiceImpl cardAdminService;

    private User testUser;
    private Card testCard;
    private final Long userId = 1L;
    private final Long cardId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("admin");

        testCard = new Card();
        testCard.setId(cardId);
        testCard.setCardNumber("1234567890123456");
        testCard.setNumberMasked("**** **** **** 3456");
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setUser(testUser);
    }

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        // Act
        Page<Card> result = cardAdminService.getAllCards(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void searchCardsByNumber_ShouldReturnFilteredCards() {
        // Arrange
        String numberPart = "3456";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        when(cardRepository.findByCardNumberContaining(numberPart, pageable)).thenReturn(cardPage);

        // Act
        Page<Card> result = cardAdminService.searchCardsByNumber(numberPart, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByCardNumberContaining(numberPart, pageable);
    }

    @Test
    void getCardsByStatus_ShouldReturnFilteredCards() {
        // Arrange
        CardStatus status = CardStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        when(cardRepository.findByStatus(status, pageable)).thenReturn(cardPage);

        // Act
        Page<Card> result = cardAdminService.getCardsByStatus(status, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByStatus(status, pageable);
    }

    @Test
    void getCardById_ShouldReturnCard() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));

        // Act
        Card result = cardAdminService.getCardById(cardId);

        // Assert
        assertNotNull(result);
        assertEquals(cardId, result.getId());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getCardById_WhenCardNotFound_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.getCardById(cardId);
        });
    }

    @Test
    void getUserCards_ShouldReturnUserCards() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(testCard));
        when(cardRepository.findByUserId(userId, pageable)).thenReturn(cardPage);

        // Act
        Page<Card> result = cardAdminService.getUserCards(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUserId(userId, pageable);
    }

    @Test
    void createCardForUser_ShouldCreateNewCard() {
        // Arrange
        Card newCard = new Card();
        newCard.setCardNumber("1234567890123456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(2L); // Simulate saved entity
            return card;
        });

        // Act
        Card result = cardAdminService.createCardForUser(userId, newCard);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        assertEquals(testUser, result.getUser());
        assertTrue(result.getNumberMasked().endsWith("3456"));
        verify(userRepository).findById(userId);
        verify(cardRepository).save(newCard);
    }

    @Test
    void createCardForUser_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            cardAdminService.createCardForUser(userId, new Card());
        });
    }

    @Test
    void blockCard_ShouldBlockCard() {
        // Arrange
        String reason = "Suspicious activity";
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // Act
        Card result = cardAdminService.blockCard(cardId, reason);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(testCard);
    }

    @Test
    void activateCard_ShouldActivateCard() {
        // Arrange
        testCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // Act
        Card result = cardAdminService.activateCard(cardId);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(testCard);
    }

    @Test
    void deleteCard_ShouldDeleteCard() {
        // Arrange
        doNothing().when(cardRepository).deleteById(cardId);

        // Act
        cardAdminService.deleteCard(cardId);

        // Assert
        verify(cardRepository).deleteById(cardId);
    }
}