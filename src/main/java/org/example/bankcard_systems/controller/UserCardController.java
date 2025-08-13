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
import org.example.bankcard_systems.dto.auth.BlockCardRequest;
import org.example.bankcard_systems.dto.auth.CardBalanceResponse;
import org.example.bankcard_systems.dto.auth.CardResponse;
import org.example.bankcard_systems.mapper.CardMapper;
import org.example.bankcard_systems.model.Card;
import org.example.bankcard_systems.security.JwtTokenProvider;
import org.example.bankcard_systems.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/cards")
@Tag(name = "User Card Management", description = "API for managing user cards")
@SecurityRequirement(name = "bearerAuth")
public class UserCardController {

    private final CardService cardService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CardMapper cardMapper;

    @Operation(
            summary = "Create new card",
            description = "Creates a new card for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card created successfully",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token) {
        Long userId = Long.valueOf(jwtTokenProvider.getId(token.replace("Bearer ", "")));
        Card newCard = cardService.createCard(userId);
        return ResponseEntity.ok(cardMapper.mapToCardResponse(newCard));
    }

    @Operation(
            summary = "Block card",
            description = "Blocks a specific card belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card blocked successfully",
                    content = @Content(schema = @Schema(implementation = Card.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user doesn't own the card"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found"
            )
    })
    @PostMapping("/block")
    public ResponseEntity<Card> blockCard(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Block card request details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BlockCardRequest.class))
            )
            @RequestBody BlockCardRequest request) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = Long.valueOf(jwtTokenProvider.getId(token));

        Card blockedCard = cardService.blockCard(
                userId,
                request.getCardId(),
                request.getMessage()
        );

        return ResponseEntity.ok(blockedCard);
    }

    @Operation(
            summary = "Get user cards",
            description = "Retrieves paginated list of cards for the authenticated user with optional search"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cards retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getUserCards(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Search term to filter cards", example = "1234")
            @RequestParam(required = false) String search,
            @Parameter(
                    description = "Pagination parameters",
                    example = "{\"page\":0,\"size\":10,\"sort\":[\"id,asc\"]}"
            )
            @PageableDefault(size = 10) Pageable pageable) {

        Long userId = Long.valueOf(jwtTokenProvider.getId(token.replace("Bearer ", "")));

        Page<Card> cards = cardService.getUserCards(userId, search, pageable);
        Page<CardResponse> response = cards.map(cardMapper::mapToCardResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get card balance",
            description = "Retrieves balance for a specific card belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CardBalanceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user doesn't own the card"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found"
            )
    })
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<CardBalanceResponse> getBalance(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the card to check balance", required = true, example = "1")
            @PathVariable Long cardId) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = Long.valueOf(jwtTokenProvider.getId(token));

        CardBalanceResponse response = cardService.getCardBalance(userId, cardId);
        return ResponseEntity.ok(response);
    }
}