package ru.merkushev.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.merkushev.abstractions.providers.ExchangeRateProvider;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.application.mappers.BankAccountContractMapper;
import ru.merkushev.application.mappers.OperationContractMapper;
import ru.merkushev.application.mappers.OperationInCurrencyContractMapper;
import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.operation.*;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.contracts.services.OperationService;
import ru.merkushev.currencies.Currency;
import ru.merkushev.exception.DomainValidationException;
import ru.merkushev.exception.EntityNotFoundException;
import ru.merkushev.exception.InsufficientFundsException;
import ru.merkushev.exchangeRates.ExchangeRate;
import ru.merkushev.operations.Operation;
import ru.merkushev.users.Role;
import ru.merkushev.users.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationServiceImpl implements OperationService {
    private final Repositories repositories;
    private final CurrentUserService currentUserService;
    private final ExchangeRateProvider exchangeRateProvider;

    @Override
    @Transactional
    public BankAccountResponse deposit(DepositRequest request) {
        if (request.getAmount().signum() < 1) {
            throw new DomainValidationException("Amount must be positive");
        }

        BankAccount account = getAccessibleAccount(request.getAccountId());

        account.deposit(request.getAmount());

        Operation operation = OperationContractMapper.toDepositOperation(request, UUID.randomUUID());

        repositories.getOperationRepository().save(operation);
        BankAccount savedAccount = repositories.getBankAccountRepository().save(account);

        return BankAccountContractMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public BankAccountResponse withdraw(WithdrawRequest request) {
        if (request.getAmount().signum() < 1) {
            throw new DomainValidationException("Amount must be positive");
        }

        BankAccount account = getAccessibleAccount(request.getAccountId());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.withdraw(request.getAmount());

        Operation operation = OperationContractMapper.toWithdrawOperation(request, UUID.randomUUID());

        repositories.getOperationRepository().save(operation);
        BankAccount savedAccount = repositories.getBankAccountRepository().save(account);

        return BankAccountContractMapper.toResponse(savedAccount);
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request.getAmount().signum() < 1) {
            throw new DomainValidationException("Amount must be positive");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new DomainValidationException("Cannot transfer to the same account");
        }

        BankAccount fromAccount = getAccessibleAccount(request.getFromAccountId());

        BankAccount toAccount = repositories.getBankAccountRepository()
                .findById(request.getToAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found"));

        User fromUser = repositories.getUserRepository()
                .findByIdWithFriends(fromAccount.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        BigDecimal commission;
        if (fromAccount.getOwnerId().equals(toAccount.getOwnerId())) {
            commission = BigDecimal.ZERO;
        } else if (fromUser.getFriends().stream()
                .anyMatch(friend -> friend.getId().equals(toAccount.getOwnerId()))) {
            commission = new BigDecimal("0.03");
        } else {
            commission = new BigDecimal("0.10");
        }

        BigDecimal commissionAmount = request.getAmount().multiply(commission);

        if (fromAccount.getBalance().compareTo(request.getAmount().add(commissionAmount)) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromAccount.withdraw(request.getAmount().add(commissionAmount));
        toAccount.deposit(request.getAmount());

        Operation outOperation = OperationContractMapper.toTransferOutOperation(
                request,
                UUID.randomUUID(),
                commissionAmount
        );

        Operation inOperation = OperationContractMapper.toTransferInOperation(
                request,
                UUID.randomUUID()
        );

        repositories.getOperationRepository().save(inOperation);
        repositories.getOperationRepository().save(outOperation);

        BankAccount savedToAccount = repositories.getBankAccountRepository().save(toAccount);
        BankAccount savedFromAccount = repositories.getBankAccountRepository().save(fromAccount);

        return new TransferResponse(
                BankAccountContractMapper.toResponse(savedFromAccount),
                BankAccountContractMapper.toResponse(savedToAccount)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationResponse> getOperations(OperationFilterRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (request.getAccountId() == null && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Access denied");
        }

        if (request.getAccountId() != null) {
            getAccessibleAccount(request.getAccountId());
        }

        return repositories.getOperationRepository()
                .findByFilter(
                        request.getAccountId(),
                        OperationContractMapper.toDomainType(request.getType())
                )
                .stream()
                .map(OperationContractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationInCurrencyResponse> getOperationsInCurrency(OperationInCurrencyFilterRequest request) {
        if (request.getAccountId() == null) {
            throw new DomainValidationException("Account id must not be null");
        }

        Currency currency = request.getCurrency();

        getAccessibleAccount(request.getAccountId());

        BigDecimal rateToRub = Currency.RUB.equals(currency)
                ? BigDecimal.ONE
                : exchangeRateProvider.getRate(currency).getRateToRub();

        return repositories.getOperationRepository()
                .findByFilter(
                        request.getAccountId(),
                        OperationContractMapper.toDomainType(request.getType())
                )
                .stream()
                .map(operation -> {
                    BigDecimal amount = operation.getAmount() == null
                            ? null
                            : operation.getAmount().divide(rateToRub, 2, RoundingMode.HALF_UP);

                    BigDecimal commission = operation.getCommission() == null
                            ? null
                            : operation.getCommission().divide(rateToRub, 2, RoundingMode.HALF_UP);

                    return OperationInCurrencyContractMapper.toResponse(
                            operation,
                            amount,
                            commission,
                            currency.name()
                    );
                })
                .toList();
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