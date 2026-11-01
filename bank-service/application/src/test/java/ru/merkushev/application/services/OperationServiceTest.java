package ru.merkushev.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.merkushev.abstractions.providers.ExchangeRateProvider;
import ru.merkushev.abstractions.repositories.BankAccountRepository;
import ru.merkushev.abstractions.repositories.OperationRepository;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.abstractions.repositories.UserRepository;
import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.operation.DepositRequest;
import ru.merkushev.contracts.operation.TransferRequest;
import ru.merkushev.contracts.operation.TransferResponse;
import ru.merkushev.contracts.operation.WithdrawRequest;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.exception.DomainValidationException;
import ru.merkushev.exception.InsufficientFundsException;
import ru.merkushev.operations.Operation;
import ru.merkushev.operations.OperationType;
import ru.merkushev.users.Role;
import ru.merkushev.users.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    @Mock
    private Repositories repositories;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    private OperationServiceImpl operationService;

    @BeforeEach
    void setUp() {
        operationService = new OperationServiceImpl(
                repositories,
                currentUserService,
                exchangeRateProvider
        );
    }

    @Test
    void deposit_shouldUpdateBalanceAndSaveOperation() {
        UUID ownerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BankAccount account = new BankAccount(
                accountId,
                ownerId,
                new BigDecimal("1000.00")
        );

        DepositRequest request = new DepositRequest();
        request.setAccountId(accountId);
        request.setAmount(new BigDecimal("250.00"));

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(ownerId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getOperationRepository()).thenReturn(operationRepository);
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankAccountResponse result = operationService.deposit(request);

        assertEquals(0, result.getBalance().compareTo(new BigDecimal("1250.00")));
        assertEquals(accountId, result.getId());

        verify(bankAccountRepository).save(account);

        ArgumentCaptor<Operation> operationCaptor =
                ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository).save(operationCaptor.capture());

        Operation savedOperation = operationCaptor.getValue();
        assertEquals(OperationType.DEPOSIT, savedOperation.getOperationType());
        assertEquals(accountId, savedOperation.getAccountId());
        assertNull(savedOperation.getRelatedAccountId());
        assertEquals(0, savedOperation.getAmount().compareTo(new BigDecimal("250.00")));
        assertEquals(0, savedOperation.getCommission().compareTo(BigDecimal.ZERO));
    }

    @Test
    void deposit_shouldThrowException_whenAmountIsNotPositive() {
        UUID accountId = UUID.randomUUID();

        DepositRequest request = new DepositRequest();
        request.setAccountId(accountId);
        request.setAmount(BigDecimal.ZERO);

        assertThrows(
                DomainValidationException.class,
                () -> operationService.deposit(request)
        );

        verifyNoInteractions(
                repositories,
                bankAccountRepository,
                userRepository,
                operationRepository,
                currentUserService
        );
    }

    @Test
    void withdraw_shouldUpdateBalanceAndSaveOperation_whenBalanceIsEnough() {
        UUID ownerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BankAccount account = new BankAccount(
                accountId,
                ownerId,
                new BigDecimal("1000.00")
        );

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(accountId);
        request.setAmount(new BigDecimal("250.00"));

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(ownerId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getOperationRepository()).thenReturn(operationRepository);
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankAccountResponse result = operationService.withdraw(request);

        assertEquals(0, result.getBalance().compareTo(new BigDecimal("750.00")));
        assertEquals(accountId, result.getId());

        verify(bankAccountRepository).save(account);

        ArgumentCaptor<Operation> operationCaptor =
                ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository).save(operationCaptor.capture());

        Operation savedOperation = operationCaptor.getValue();
        assertEquals(OperationType.WITHDRAW, savedOperation.getOperationType());
        assertEquals(accountId, savedOperation.getAccountId());
        assertNull(savedOperation.getRelatedAccountId());
        assertEquals(0, savedOperation.getAmount().compareTo(new BigDecimal("250.00")));
        assertEquals(0, savedOperation.getCommission().compareTo(BigDecimal.ZERO));
    }

    @Test
    void withdraw_shouldThrowException_whenBalanceIsNotEnough() {
        UUID ownerId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BankAccount account = new BankAccount(
                accountId,
                ownerId,
                new BigDecimal("100.00")
        );

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(accountId);
        request.setAmount(new BigDecimal("250.00"));

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(ownerId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(
                InsufficientFundsException.class,
                () -> operationService.withdraw(request)
        );

        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(operationRepository);
    }

    @Test
    void transfer_shouldUseZeroCommission_betweenOwnAccounts() {
        UUID ownerId = UUID.randomUUID();
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        BankAccount fromAccount = new BankAccount(
                fromAccountId, ownerId, new BigDecimal("1000.00")
        );
        BankAccount toAccount = new BankAccount(
                toAccountId, ownerId, new BigDecimal("200.00")
        );

        User owner = mock(User.class);

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(ownerId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal("300.00"));

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getUserRepository()).thenReturn(userRepository);
        when(repositories.getOperationRepository()).thenReturn(operationRepository);

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));
        when(userRepository.findByIdWithFriends(ownerId))
                .thenReturn(Optional.of(owner));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferResponse result = operationService.transfer(request);

        assertEquals(
                0,
                result.getFromAccount().getBalance()
                        .compareTo(new BigDecimal("700.00"))
        );
        assertEquals(
                0,
                result.getToAccount().getBalance()
                        .compareTo(new BigDecimal("500.00"))
        );

        verify(bankAccountRepository).save(fromAccount);
        verify(bankAccountRepository).save(toAccount);

        ArgumentCaptor<Operation> operationCaptor =
                ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository, times(2))
                .save(operationCaptor.capture());

        List<Operation> operations = operationCaptor.getAllValues();

        Operation outOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_OUT)
                .findFirst()
                .orElseThrow();

        Operation inOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_IN)
                .findFirst()
                .orElseThrow();

        assertEquals(
                0,
                outOperation.getCommission().compareTo(BigDecimal.ZERO)
        );
        assertEquals(
                0,
                inOperation.getCommission().compareTo(BigDecimal.ZERO)
        );
        assertEquals(
                0,
                outOperation.getAmount().compareTo(new BigDecimal("300.00"))
        );
        assertEquals(
                0,
                inOperation.getAmount().compareTo(new BigDecimal("300.00"))
        );
    }

    @Test
    void transfer_shouldUseThreePercentCommission_forFriend() {
        UUID fromUserId = UUID.randomUUID();
        UUID friendUserId = UUID.randomUUID();
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        BankAccount fromAccount = new BankAccount(
                fromAccountId, fromUserId, new BigDecimal("1000.00")
        );
        BankAccount toAccount = new BankAccount(
                toAccountId, friendUserId, new BigDecimal("50.00")
        );

        User friend = mock(User.class);
        when(friend.getId()).thenReturn(friendUserId);

        Set<User> friends = new HashSet<>();
        friends.add(friend);

        User fromUser = mock(User.class);
        when(fromUser.getFriends()).thenReturn(friends);

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(fromUserId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal("100.00"));

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getUserRepository()).thenReturn(userRepository);
        when(repositories.getOperationRepository()).thenReturn(operationRepository);

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));
        when(userRepository.findByIdWithFriends(fromUserId))
                .thenReturn(Optional.of(fromUser));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferResponse result = operationService.transfer(request);

        assertEquals(
                0,
                result.getFromAccount().getBalance()
                        .compareTo(new BigDecimal("897.00"))
        );
        assertEquals(
                0,
                result.getToAccount().getBalance()
                        .compareTo(new BigDecimal("150.00"))
        );

        ArgumentCaptor<Operation> operationCaptor =
                ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository, times(2))
                .save(operationCaptor.capture());

        List<Operation> operations = operationCaptor.getAllValues();

        Operation outOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_OUT)
                .findFirst()
                .orElseThrow();

        Operation inOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_IN)
                .findFirst()
                .orElseThrow();

        assertEquals(
                0,
                outOperation.getCommission()
                        .compareTo(new BigDecimal("3.00"))
        );
        assertEquals(
                0,
                inOperation.getCommission().compareTo(BigDecimal.ZERO)
        );
    }

    @Test
    void transfer_shouldUseTenPercentCommission_forOtherUser() {
        UUID fromUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        BankAccount fromAccount = new BankAccount(
                fromAccountId, fromUserId, new BigDecimal("1000.00")
        );
        BankAccount toAccount = new BankAccount(
                toAccountId, otherUserId, new BigDecimal("10.00")
        );

        User fromUser = mock(User.class);
        when(fromUser.getFriends()).thenReturn(new HashSet<>());

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(fromUserId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal("100.00"));

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getUserRepository()).thenReturn(userRepository);
        when(repositories.getOperationRepository()).thenReturn(operationRepository);

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));
        when(userRepository.findByIdWithFriends(fromUserId))
                .thenReturn(Optional.of(fromUser));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        when(bankAccountRepository.save(any(BankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(operationRepository.save(any(Operation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransferResponse result = operationService.transfer(request);

        assertEquals(
                0,
                result.getFromAccount().getBalance()
                        .compareTo(new BigDecimal("890.00"))
        );
        assertEquals(
                0,
                result.getToAccount().getBalance()
                        .compareTo(new BigDecimal("110.00"))
        );

        ArgumentCaptor<Operation> operationCaptor =
                ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository, times(2))
                .save(operationCaptor.capture());

        List<Operation> operations = operationCaptor.getAllValues();

        Operation outOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_OUT)
                .findFirst()
                .orElseThrow();

        Operation inOperation = operations.stream()
                .filter(op -> op.getOperationType() == OperationType.TRANSFER_IN)
                .findFirst()
                .orElseThrow();

        assertEquals(
                0,
                outOperation.getCommission()
                        .compareTo(new BigDecimal("10.00"))
        );
        assertEquals(
                0,
                inOperation.getCommission().compareTo(BigDecimal.ZERO)
        );
    }

    @Test
    void transfer_shouldThrowException_whenNotEnoughMoneyIncludingCommission() {
        UUID fromUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();

        BankAccount fromAccount = new BankAccount(
                fromAccountId, fromUserId, new BigDecimal("100.00")
        );
        BankAccount toAccount = new BankAccount(
                toAccountId, otherUserId, new BigDecimal("10.00")
        );

        User fromUser = mock(User.class);
        when(fromUser.getFriends()).thenReturn(new HashSet<>());

        User currentUser = mock(User.class);
        when(currentUser.getId()).thenReturn(fromUserId);
        when(currentUser.getRole()).thenReturn(Role.CLIENT);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(fromAccountId);
        request.setToAccountId(toAccountId);
        request.setAmount(new BigDecimal("100.00"));

        when(repositories.getBankAccountRepository()).thenReturn(bankAccountRepository);
        when(repositories.getUserRepository()).thenReturn(userRepository);

        when(bankAccountRepository.findById(fromAccountId))
                .thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findById(toAccountId))
                .thenReturn(Optional.of(toAccount));
        when(userRepository.findByIdWithFriends(fromUserId))
                .thenReturn(Optional.of(fromUser));
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(
                InsufficientFundsException.class,
                () -> operationService.transfer(request)
        );

        verifyNoInteractions(operationRepository);
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }
}
