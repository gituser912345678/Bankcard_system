package org.example.bankcard_systems.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.bankcard_systems.model.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Setter
@Getter
public class CardResponse {

    private Long id;
    private String numberMasked;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private UserSafeDto user;

}