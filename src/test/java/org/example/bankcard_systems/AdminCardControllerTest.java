package org.example.bankcard_systems;

import org.example.bankcard_systems.controller.AdminCardController;
import org.example.bankcard_systems.dto.auth.AdminCardDto;
import org.example.bankcard_systems.dto.auth.AdminCardResponse;
import org.example.bankcard_systems.mapper.AdminCardMapper;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.service.CardAdminService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardControllerTest {

    @Mock
    private CardAdminService cardAdminService;

    @Mock
    private AdminCardMapper adminCardMapper;

    @InjectMocks
    private AdminCardController adminCardController;

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10);
        Card card = new Card();
        AdminCardResponse response = new AdminCardResponse();
        Page<Card> cardPage = new PageImpl<>(List.of(card));

        when(cardAdminService.getAllCards(any(Pageable.class))).thenReturn(cardPage);
        when(adminCardMapper.mapToAdminCardResponse(any(Card.class))).thenReturn(response);

        ResponseEntity<Page<AdminCardResponse>> result = adminCardController.getAllCards(pageable);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().getTotalElements());
        verify(cardAdminService).getAllCards(pageable);
        verify(adminCardMapper).mapToAdminCardResponse(card);
    }

    @Test
    void createCardForUser_ShouldReturnCreatedCard() {
        Long userId = 1L;
        AdminCardDto requestDto = new AdminCardDto();
        Card card = new Card();
        AdminCardDto responseDto = new AdminCardDto();

        when(adminCardMapper.mapToEntity(any(AdminCardDto.class))).thenReturn(card);
        when(cardAdminService.createCardForUser(anyLong(), any(Card.class))).thenReturn(card);
        when(adminCardMapper.mapToDto(any(Card.class))).thenReturn(responseDto);

        ResponseEntity<AdminCardDto> result = adminCardController.createCardForUser(userId, requestDto);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(responseDto, result.getBody());
        verify(adminCardMapper).mapToEntity(requestDto);
        verify(cardAdminService).createCardForUser(userId, card);
        verify(adminCardMapper).mapToDto(card);
    }

    @Test
    void blockCard_ShouldReturnBlockedCard() {
        Long cardId = 1L;
        String reason = "Suspicious activity";
        Card blockedCard = new Card();
        AdminCardDto responseDto = new AdminCardDto();

        when(cardAdminService.blockCard(anyLong(), anyString())).thenReturn(blockedCard);
        when(adminCardMapper.mapToDto(any(Card.class))).thenReturn(responseDto);

        ResponseEntity<AdminCardDto> result = adminCardController.blockCard(cardId, reason);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(responseDto, result.getBody());
        verify(cardAdminService).blockCard(cardId, reason);
        verify(adminCardMapper).mapToDto(blockedCard);
    }

    @Test
    void blockCard_WithoutReason_ShouldWork() {
        Long cardId = 1L;
        Card blockedCard = new Card();
        AdminCardDto responseDto = new AdminCardDto();

        when(cardAdminService.blockCard(anyLong(), isNull())).thenReturn(blockedCard);
        when(adminCardMapper.mapToDto(any(Card.class))).thenReturn(responseDto);

        ResponseEntity<AdminCardDto> result = adminCardController.blockCard(cardId, null);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        verify(cardAdminService).blockCard(cardId, null);
    }

    @Test
    void activateCard_ShouldReturnActivatedCard() {
        Long cardId = 1L;
        Card activatedCard = new Card();
        AdminCardDto responseDto = new AdminCardDto();

        when(cardAdminService.activateCard(anyLong())).thenReturn(activatedCard);
        when(adminCardMapper.mapToDto(any(Card.class))).thenReturn(responseDto);

        ResponseEntity<AdminCardDto> result = adminCardController.activateCard(cardId);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(responseDto, result.getBody());
        verify(cardAdminService).activateCard(cardId);
        verify(adminCardMapper).mapToDto(activatedCard);
    }

    @Test
    void deleteCard_ShouldReturnNoContent() {
        // Arrange
        Long cardId = 1L;
        doNothing().when(cardAdminService).deleteCard(anyLong());

        // Act
        ResponseEntity<Void> result = adminCardController.deleteCard(cardId);

        // Assert
        assertNotNull(result);
        assertEquals(204, result.getStatusCodeValue());
        verify(cardAdminService).deleteCard(cardId);
    }

    @Test
    void getAllCards_EmptyResult_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> emptyPage = new PageImpl<>(Collections.emptyList());

        when(cardAdminService.getAllCards(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<AdminCardResponse>> result = adminCardController.getAllCards(pageable);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().isEmpty());
        verify(cardAdminService).getAllCards(pageable);
        verify(adminCardMapper, never()).mapToAdminCardResponse(any());
    }
}