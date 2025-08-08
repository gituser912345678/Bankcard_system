package org.example.bankcard_systems.controller.impl;

import org.example.bankcard_systems.controller.AuthController;
import org.example.bankcard_systems.dto.auth.JwtRefreshRequest;
import org.example.bankcard_systems.dto.auth.JwtAuthRequest;
import org.example.bankcard_systems.dto.auth.JwtAuthResponse;
import org.example.bankcard_systems.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authServiceImpl;

    @Override
    public ResponseEntity<JwtAuthResponse> login(JwtAuthRequest loginRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authServiceImpl.login(loginRequest));
    }

    @Override
    public ResponseEntity<Void> register(JwtAuthRequest request) {
        authServiceImpl.save(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    public ResponseEntity<JwtAuthResponse> refresh(JwtRefreshRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authServiceImpl.refresh(request.getRefreshToken()));
    }
}
