package org.example.bankcard_systems;

import org.example.bankcard_systems.controller.UserCardController;
import org.example.bankcard_systems.dto.auth.CardResponse;
import org.example.bankcard_systems.mapper.CardMapper;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.security.JwtTokenProvider;
import org.example.bankcard_systems.service.CardService;
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
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private UserCardController userCardController;

    private final String validToken = "Bearer validToken123";
    private final Long userId = 1L;
    private final Long cardId = 5L;

    @BeforeEach
    void setUp() {
        when(jwtTokenProvider.getId("validToken123")).thenReturn(userId.toString());
    }

    @Test
    void createCard_ShouldReturnNewCard() {
        Card newCard = new Card();
        CardResponse cardResponse = new CardResponse();

        when(cardService.createCard(userId)).thenReturn(newCard);
        when(cardMapper.mapToCardResponse(newCard)).thenReturn(cardResponse);

        ResponseEntity<CardResponse> response = userCardController.createCard(validToken);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cardResponse, response.getBody());
        verify(cardService).createCard(userId);
        verify(cardMapper).mapToCardResponse(newCard);
    }

    @Test
    void getUserCards_ShouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);
        String search = "1234";
        Card card = new Card();
        CardResponse cardResponse = new CardResponse();
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardService.getUserCards(userId, search, pageable)).thenReturn(cardPage);
        when(cardMapper.mapToCardResponse(card)).thenReturn(cardResponse);

        ResponseEntity<Page<CardResponse>> response =
                userCardController.getUserCards(validToken, search, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
        verify(cardService).getUserCards(userId, search, pageable);
        verify(cardMapper).mapToCardResponse(card);
    }

    @Test
    void getUserCards_WithoutSearch_ShouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Card card = new Card();
        CardResponse cardResponse = new CardResponse();
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardService.getUserCards(userId, null, pageable)).thenReturn(cardPage);
        when(cardMapper.mapToCardResponse(card)).thenReturn(cardResponse);

        ResponseEntity<Page<CardResponse>> response =
                userCardController.getUserCards(validToken, null, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(cardService).getUserCards(userId, null, pageable);
    }

    @Test
    void getUserCards_EmptyResult_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> emptyPage = new PageImpl<>(Collections.emptyList());

        when(cardService.getUserCards(userId, null, pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<CardResponse>> response =
                userCardController.getUserCards(validToken, null, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(cardService).getUserCards(userId, null, pageable);
        verify(cardMapper, never()).mapToCardResponse(any());
    }
}