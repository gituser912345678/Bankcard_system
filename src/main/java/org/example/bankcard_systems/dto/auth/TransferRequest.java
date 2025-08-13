package org.example.bankcard_systems.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class TransferRequest {

    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String message;

}