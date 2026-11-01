
package ru.merkushev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.enums.OperationTypeDto;
import ru.merkushev.contracts.operation.*;
import ru.merkushev.contracts.services.OperationService;
import ru.merkushev.currencies.Currency;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/operations")
@Validated
@Tag(
        name = "Operations",
        description = "Operations with deposits, withdrawals, transfers and operation history"
)
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @Operation(summary = "Deposit money to account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposit completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/deposits")
    public ResponseEntity<BankAccountResponse> deposit(
            @Valid @RequestBody DepositRequest request
    ) {
        return ResponseEntity.ok(operationService.deposit(request));
    }

    @Operation(summary = "Withdraw money from account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/withdrawals")
    public ResponseEntity<BankAccountResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request
    ) {
        return ResponseEntity.ok(operationService.withdraw(request));
    }

    @Operation(summary = "Transfer money between accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds"),
            @ApiResponse(responseCode = "404", description = "Account or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        return ResponseEntity.ok(operationService.transfer(request));
    }

    @Operation(summary = "Get all operations with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operations returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<OperationResponse>> getOperations(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) OperationTypeDto type
    ) {
        OperationFilterRequest request = new OperationFilterRequest();
        request.setAccountId(accountId);
        request.setType(type);

        return ResponseEntity.ok(operationService.getOperations(request));
    }

    @Operation(summary = "Get account operation history in selected currency")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operations returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Account or exchange rate not found"),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/in-currency")
    public ResponseEntity<List<OperationInCurrencyResponse>> getOperationsInCurrency(
            @RequestParam UUID accountId,
            @RequestParam Currency currency,
            @RequestParam(required = false) OperationTypeDto type
    ) {
        OperationInCurrencyFilterRequest request =
                new OperationInCurrencyFilterRequest();

        request.setAccountId(accountId);
        request.setCurrency(currency);
        request.setType(type);

        return ResponseEntity.ok(
                operationService.getOperationsInCurrency(request)
        );
    }
}
