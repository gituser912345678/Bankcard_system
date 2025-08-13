package org.example.bankcard_systems.dto.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.bankcard_systems.model.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class AdminCardResponse {
    private Long id;
    private String numberMasked;  // Номер карты с маскировкой (****1234)
    private LocalDate expiryDate;    // Срок действия "MM/YY"
    private CardStatus status;   // ACTIVE, BLOCKED и т.д.
    private BigDecimal balance;  // Текущий баланс
    private Long userId;
}