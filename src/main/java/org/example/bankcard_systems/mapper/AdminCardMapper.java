package org.example.bankcard_systems.mapper;

import org.example.bankcard_systems.dto.auth.AdminCardDto;
import org.example.bankcard_systems.dto.auth.AdminCardResponse;
import org.example.bankcard_systems.model.Card;
import org.springframework.stereotype.Component;

@Component
public class AdminCardMapper {

    public AdminCardResponse mapToAdminCardResponse(Card card) {
        AdminCardResponse response = new AdminCardResponse();
        response.setId(card.getId());
        response.setNumberMasked(card.getNumberMasked());
        response.setExpiryDate(card.getExpiryDate());
        response.setStatus(card.getStatus());
        response.setBalance(card.getBalance());
        response.setUserId(card.getUser().getId());
        return response;
    }

    public AdminCardDto mapToDto(Card card) {
        AdminCardDto dto = new AdminCardDto();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setNumberMasked(card.getNumberMasked());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        dto.setUserId(card.getUser().getId());
        return dto;
    }

    public Card mapToEntity(AdminCardDto dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setNumberMasked(dto.getNumberMasked());
        card.setExpiryDate(dto.getExpiryDate());
        card.setStatus(dto.getStatus());
        card.setBalance(dto.getBalance());
        return card;
    }

}