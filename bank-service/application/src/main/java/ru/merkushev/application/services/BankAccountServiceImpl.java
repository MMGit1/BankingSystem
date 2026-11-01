package ru.merkushev.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.merkushev.abstractions.providers.ExchangeRateProvider;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.application.mappers.BankAccountContractMapper;
import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.contracts.account.BalanceInCurrencyResponse;
import ru.merkushev.contracts.account.BalanceResponse;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.account.CreateAccountRequest;
import ru.merkushev.contracts.services.BankAccountService;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.currencies.Currency;
import ru.merkushev.exception.DomainValidationException;
import ru.merkushev.exception.EntityNotFoundException;
import ru.merkushev.exchangeRates.ExchangeRate;
import ru.merkushev.users.Role;
import ru.merkushev.users.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final Repositories repositories;
    private final CurrentUserService currentUserService;
    private final ExchangeRateProvider exchangeRateProvider;

    @Override
    public BankAccountResponse createAccount(CreateAccountRequest request) {
        User owner = repositories.getUserRepository()
                .findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (owner.getRole() == Role.ADMIN) {
            throw new DomainValidationException("Admin cannot have bank accounts");
        }

        BankAccount newBankAccount = BankAccountContractMapper.toDomain(request, UUID.randomUUID());
        BankAccount savedAccount = repositories.getBankAccountRepository().save(newBankAccount);

        return BankAccountContractMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponse getBankAccountById(UUID accountId) {
        BankAccount account = getAccessibleAccount(accountId);
        return BankAccountContractMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID accountId) {
        BankAccount account = getAccessibleAccount(accountId);
        return BankAccountContractMapper.toBalanceResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAccountsByUserId(UUID userId) {
        User user = repositories.getUserRepository()
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() != Role.CLIENT) {
            throw new EntityNotFoundException("User not found");
        }

        return repositories.getBankAccountRepository()
                .findByOwnerId(userId)
                .stream()
                .map(BankAccountContractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponse> getAllAccounts() {
        return repositories.getBankAccountRepository()
                .findAll()
                .stream()
                .map(BankAccountContractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceInCurrencyResponse getBalanceInCurrency(UUID accountId, Currency currency) {
        BankAccount account = getAccessibleAccount(accountId);

        if (Currency.RUB.equals(currency)) {
            return new BalanceInCurrencyResponse(
                    account.getId(),
                    account.getBalance(),
                    Currency.RUB
            );
        }

        ExchangeRate exchangeRate = exchangeRateProvider.getRate(currency);

        BigDecimal convertedBalance = account.getBalance()
                .divide(exchangeRate.getRateToRub(), 2, RoundingMode.HALF_UP);

        return new BalanceInCurrencyResponse(
                account.getId(),
                convertedBalance,
                currency
        );
    }

    private BankAccount getAccessibleAccount(UUID accountId) {
        BankAccount account = repositories.getBankAccountRepository()
                .findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found"));

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return account;
        }

        if (!account.getOwnerId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return account;
    }
}