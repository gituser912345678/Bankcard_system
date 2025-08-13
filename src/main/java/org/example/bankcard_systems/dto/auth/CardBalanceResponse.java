package org.example.bankcard_systems.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CardBalanceResponse {

    private Long cardId;
    private String maskedNumber;
    private BigDecimal balance;

    public CardBalanceResponse(Long cardId, String maskedNumber, BigDecimal balance) {

        this.cardId = cardId;
        this.maskedNumber = maskedNumber;
        this.balance = balance;

    }
}