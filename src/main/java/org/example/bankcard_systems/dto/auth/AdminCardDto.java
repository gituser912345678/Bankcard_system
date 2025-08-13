package org.example.bankcard_systems.dto.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.bankcard_systems.model.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class AdminCardDto {

    private Long id;
    private String cardNumber;
    private String numberMasked;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private Long userId;

}