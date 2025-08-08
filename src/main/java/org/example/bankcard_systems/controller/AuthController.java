package org.example.bankcard_systems.controller;

import org.example.bankcard_systems.dto.auth.JwtAuthRequest;
import org.example.bankcard_systems.dto.auth.JwtAuthResponse;
import org.example.bankcard_systems.dto.auth.JwtRefreshRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid JwtAuthRequest loginRequest);

    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody @Valid JwtAuthRequest request);

    @PostMapping("/refresh")
    ResponseEntity<JwtAuthResponse> refresh(@RequestBody @Valid JwtRefreshRequest request);
}
