package org.example.bankcard_systems.service;

import org.example.bankcard_systems.dto.auth.JwtAuthRequest;
import org.example.bankcard_systems.dto.auth.JwtAuthResponse;

public interface AuthService {

    JwtAuthResponse login(JwtAuthRequest loginRequest);

    JwtAuthResponse refresh(String refreshToken);

    void save(JwtAuthRequest request);

}
