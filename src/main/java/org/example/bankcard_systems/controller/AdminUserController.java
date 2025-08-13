package org.example.bankcard_systems.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bankcard_systems.model.Role;
import org.example.bankcard_systems.service.UserAdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.bankcard_systems.dto.admin.UserDto;
import org.example.bankcard_systems.mapper.admin.UserMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Tag(name = "Admin User Management", description = "API for managing users by administrators")
public class AdminUserController {

    private final UserAdminService userAdminService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Get all users",
            description = "Retrieves a paginated list of all users in the system",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of items per page", example = "10"),
                    @Parameter(name = "sort", description = "Sorting criteria in the format: property(,asc|desc)", example = "name,asc")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of users",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(
                userAdminService.getAllUsers(pageable)
                        .map(userMapper::mapToUserDto)
        );
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found and returned",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "ID of the user to be retrieved", required = true, example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                userMapper.mapToUserDto(userAdminService.getUserById(userId))
        );
    }

    @Operation(
            summary = "Update user roles",
            description = "Updates the roles assigned to a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User roles updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @PutMapping("/{userId}/roles")
    public ResponseEntity<UserDto> updateUserRoles(
            @Parameter(description = "ID of the user whose roles will be updated", required = true, example = "1")
            @PathVariable Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Set of roles to assign to the user",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Set.class))
            )
            @RequestBody Set<Role> roles) {
        return ResponseEntity.ok(
                userMapper.mapToUserDto(userAdminService.updateUserRoles(userId, roles))
        );
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user from the system by their ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true, example = "1")
            @PathVariable Long userId) {
        userAdminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}