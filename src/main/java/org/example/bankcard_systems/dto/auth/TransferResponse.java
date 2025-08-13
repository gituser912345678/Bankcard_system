package org.example.bankcard_systems.dto.auth;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferResponse {

    private Long transactionId;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String status;

    public TransferResponse(Long fromCardId, Long toCardId, BigDecimal amount, String status) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.status = status;
    }
}