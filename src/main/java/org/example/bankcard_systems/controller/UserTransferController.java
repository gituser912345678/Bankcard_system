package org.example.bankcard_systems.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bankcard_systems.security.JwtTokenProvider;
import org.example.bankcard_systems.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.example.bankcard_systems.dto.auth.TransferRequest;
import org.example.bankcard_systems.dto.auth.TransferResponse;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/transfer")
@Tag(name = "User Transfer Operations", description = "API for managing money transfers between user cards")
@SecurityRequirement(name = "bearerAuth")
public class UserTransferController {

    private final CardService cardService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "Transfer between own cards",
            description = "Transfers money between cards belonging to the same authenticated user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transfer details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransferRequest.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Basic transfer",
                                            value = "{\"fromCardId\": 1, \"toCardId\": 2, \"amount\": 100.0, \"message\": \"Monthly savings\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransferResponse.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Success response",
                                            value = "{\"transactionId\": \"123e4567-e89b-12d3-a456-426614174000\", \"status\": \"COMPLETED\", \"message\": \"Transfer successful\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid transfer data",
                    content = @Content(
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Insufficient funds",
                                            value = "{\"status\": \"FAILED\", \"message\": \"Insufficient funds on source card\"}"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Same card",
                                            value = "{\"status\": \"FAILED\", \"message\": \"Source and destination cards must be different\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user doesn't own one of the cards"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - one of the cards doesn't exist"
            )
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferBetweenCards(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid TransferRequest request) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = Long.valueOf(jwtTokenProvider.getId(token));

        TransferResponse response = cardService.transferBetweenOwnCards(userId, request);
        return ResponseEntity.ok(response);
    }
}