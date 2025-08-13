package org.example.bankcard_systems.mapper;

import org.example.bankcard_systems.dto.auth.CardResponse;
import org.example.bankcard_systems.dto.auth.UserSafeDto;
import org.example.bankcard_systems.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponse mapToCardResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setNumberMasked(card.getNumberMasked());
        response.setExpiryDate(card.getExpiryDate());
        response.setStatus(card.getStatus());
        response.setBalance(card.getBalance());

        // Создаем безопасное DTO для пользователя
        UserSafeDto userDto = new UserSafeDto();
        userDto.setId(card.getUser().getId());
        userDto.setUsername(card.getUser().getUsername());

        response.setUser(userDto);
        return response;
    }

}