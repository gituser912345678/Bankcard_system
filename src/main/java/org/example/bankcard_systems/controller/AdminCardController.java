package org.example.bankcard_systems.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bankcard_systems.dto.auth.AdminCardDto;
import org.example.bankcard_systems.dto.auth.AdminCardResponse;
import org.example.bankcard_systems.mapper.AdminCardMapper;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.service.CardAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
@Tag(name = "Admin Card Management", description = "API for managing cards by administrators")
public class AdminCardController {

    private final CardAdminService cardAdminService;
    private final AdminCardMapper adminCardMapper;

    @Operation(
            summary = "Get all cards",
            description = "Retrieve paginated list of all cards in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved cards",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdminCardResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - admin access required"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<AdminCardResponse>> getAllCards(
            @Parameter(
                    description = "Pagination and sorting parameters",
                    example = "{\"page\":0,\"size\":10,\"sort\":[\"id,asc\"]}"
            )
            Pageable pageable) {
        Page<AdminCardResponse> response = cardAdminService.getAllCards(pageable)
                .map(adminCardMapper::mapToAdminCardResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create card for user",
            description = "Create a new card for specified user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdminCardDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid card data"
                    )
            }
    )
    @PostMapping("/user/{userId}")
    public ResponseEntity<AdminCardDto> createCardForUser(
            @Parameter(description = "ID of the user to create card for", required = true, example = "1")
            @PathVariable Long userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card creation data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminCardDto.class)
                    )
            )
            @RequestBody AdminCardDto cardDto) {
        Card card = adminCardMapper.mapToEntity(cardDto);
        return ResponseEntity.ok(
                adminCardMapper.mapToDto(cardAdminService.createCardForUser(userId, card))
        );
    }

    @Operation(
            summary = "Block card",
            description = "Block an existing card with optional reason",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card blocked successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdminCardDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found"
                    )
            }
    )
    @PatchMapping("/{cardId}/block")
    public ResponseEntity<AdminCardDto> blockCard(
            @Parameter(description = "ID of the card to block", required = true, example = "5")
            @PathVariable Long cardId,

            @Parameter(description = "Reason for blocking", example = "Suspicious activity")
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(
                adminCardMapper.mapToDto(cardAdminService.blockCard(cardId, reason))
        );
    }

    @Operation(
            summary = "Activate card",
            description = "Activate a previously blocked card",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Card activated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdminCardDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found"
                    )
            }
    )
    @PatchMapping("/{cardId}/activate")
    public ResponseEntity<AdminCardDto> activateCard(
            @Parameter(description = "ID of the card to activate", required = true, example = "5")
            @PathVariable Long cardId) {
        return ResponseEntity.ok(
                adminCardMapper.mapToDto(cardAdminService.activateCard(cardId))
        );
    }

    @Operation(
            summary = "Delete card",
            description = "Permanently delete a card from the system",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Card deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - card cannot be deleted (has active transactions)"
                    )
            }
    )
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID of the card to delete", required = true, example = "5")
            @PathVariable Long cardId) {
        cardAdminService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}