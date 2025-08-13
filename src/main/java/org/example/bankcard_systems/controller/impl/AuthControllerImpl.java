package org.example.bankcard_systems.controller.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthControllerImpl implements AuthController {

    private final AuthService authServiceImpl;

    @Override
    @Operation(
            summary = "User login",
            description = "Authenticate user and return JWT tokens",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated",
                            content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid credentials",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<JwtAuthResponse> login(JwtAuthRequest loginRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authServiceImpl.login(loginRequest));
    }

    @Override
    @Operation(
            summary = "User registration",
            description = "Register new user in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully registered",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - invalid data",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - username already exists",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<Void> register(JwtAuthRequest request) {
        authServiceImpl.save(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Override
    @Operation(
            summary = "Refresh token",
            description = "Get new access token using refresh token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully refreshed token",
                            content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid refresh token",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<JwtAuthResponse> refresh(JwtRefreshRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authServiceImpl.refresh(request.getRefreshToken()));
    }
}