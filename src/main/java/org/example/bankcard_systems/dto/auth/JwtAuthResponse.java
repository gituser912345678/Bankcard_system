package org.example.bankcard_systems.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthResponse {

    private Long id;
    private String username;
    private String accessToken;
    private String refreshToken;

}
