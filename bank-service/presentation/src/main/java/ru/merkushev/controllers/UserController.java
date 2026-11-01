package ru.merkushev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.enums.GenderDto;
import ru.merkushev.contracts.services.BankAccountService;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.contracts.services.UserService;
import ru.merkushev.contracts.user.AddFriendRequest;
import ru.merkushev.contracts.user.CreateUserRequest;
import ru.merkushev.contracts.user.UserFilterRequest;
import ru.merkushev.contracts.user.UserResponse;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations with users and their friends")
public class UserController {
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    @Operation(summary = "Get current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(currentUserService.getCurrentUserResponse());
    }

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid user id"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get all users with optional filters by hair color and gender")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) String hairColor,
            @RequestParam(required = false) GenderDto gender
    ) {
        UserFilterRequest request = new UserFilterRequest();
        request.setHairColor(hairColor);
        request.setGender(gender);

        return ResponseEntity.ok(userService.getUsers(request));
    }

    @Operation(summary = "Add friend to current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend added"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/me/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable UUID friendId) {
        AddFriendRequest request = new AddFriendRequest();
        request.setUserId(currentUserService.getCurrentUser().getId());
        request.setFriendId(friendId);

        userService.addFriend(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove friend from current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Friend removed"),
            @ApiResponse(responseCode = "400", description = "Invalid ids"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Friend not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/me/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable UUID friendId) {
        userService.removeFriend(currentUserService.getCurrentUser().getId(), friendId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get friends of current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friends returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me/friends")
    public ResponseEntity<List<UserResponse>> getFriends() {
        return ResponseEntity.ok(userService.getFriends(currentUserService.getCurrentUser().getId()));
    }

    @Operation(summary = "Get accounts by user id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned"),
            @ApiResponse(responseCode = "400", description = "Invalid user id"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<List<BankAccountResponse>> getAccountsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(bankAccountService.getAccountsByUserId(userId));
    }
}