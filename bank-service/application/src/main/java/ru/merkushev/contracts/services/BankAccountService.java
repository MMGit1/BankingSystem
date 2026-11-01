package ru.merkushev.contracts.services;

import ru.merkushev.contracts.account.BalanceInCurrencyResponse;
import ru.merkushev.contracts.account.BalanceResponse;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.account.CreateAccountRequest;
import ru.merkushev.currencies.Currency;

import java.util.List;
import java.util.UUID;

public interface BankAccountService {
    BankAccountResponse createAccount(CreateAccountRequest request);

    BankAccountResponse getBankAccountById(UUID accountId);

    BalanceResponse getBalance(UUID accountId);

    List<BankAccountResponse> getAccountsByUserId(UUID userId);

    List<BankAccountResponse> getAllAccounts();

    BalanceInCurrencyResponse getBalanceInCurrency(UUID accountId, Currency currency);
}
