package ru.merkushev.application.mappers;

import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.contracts.account.BalanceResponse;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.account.CreateAccountRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountContractMapper {
    public static BankAccount toDomain(CreateAccountRequest request, UUID accountId) {
        return new BankAccount(
                accountId,
                request.getUserId(),
                BigDecimal.ZERO
        );
    }

    public static BankAccountResponse toResponse(BankAccount account) {
        return new BankAccountResponse(
                account.getId(),
                account.getOwnerId(),
                account.getBalance()
        );
    }

    public static BalanceResponse toBalanceResponse(BankAccount account) {
        return new BalanceResponse(
                account.getId(),
                account.getBalance()
        );
    }
}
