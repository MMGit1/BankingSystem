package ru.merkushev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.merkushev.contracts.account.BalanceInCurrencyResponse;
import ru.merkushev.contracts.account.BalanceResponse;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.account.CreateAccountRequest;
import ru.merkushev.contracts.services.BankAccountService;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.currencies.Currency;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
@Validated
@Tag(name = "Accounts", description = "Operations with bank accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Create a new bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(201).body(bankAccountService.createAccount(request));
    }

    @Operation(summary = "Get current authenticated user's accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user's accounts returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<List<BankAccountResponse>> getCurrentUserAccounts() {
        return ResponseEntity.ok(
                bankAccountService.getAccountsByUserId(currentUserService.getCurrentUser().getId())
        );
    }

    @Operation(summary = "Get all accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accounts returned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }

    @Operation(summary = "Get bank account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "400", description = "Invalid account id"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountResponse> getBankAccountById(@PathVariable UUID accountId) {
        return ResponseEntity.ok(bankAccountService.getBankAccountById(accountId));
    }

    @Operation(summary = "Get account balance by account id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance returned"),
            @ApiResponse(responseCode = "400", description = "Invalid account id"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID accountId) {
        return ResponseEntity.ok(bankAccountService.getBalance(accountId));
    }

    @Operation(summary = "Get account balance in selected currency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance in currency returned"),
            @ApiResponse(responseCode = "400", description = "Invalid account id or currency"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Account or exchange rate not found"),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{accountId}/balance-in-currency")
    public ResponseEntity<BalanceInCurrencyResponse> getBalanceInCurrency(
            @PathVariable UUID accountId,
            @RequestParam
            @Pattern(
                    regexp = "^[A-Z]{3}$",
                    message = "Currency code must contain exactly 3 uppercase latin letters"
            )
            Currency currency
    ) {
        return ResponseEntity.ok(bankAccountService.getBalanceInCurrency(accountId, currency));
    }
}