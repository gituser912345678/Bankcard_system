package org.example.bankcard_systems.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockCardRequest {

    private Long cardId;
    private String message;

}
