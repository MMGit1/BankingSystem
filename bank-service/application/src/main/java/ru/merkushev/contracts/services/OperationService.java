package ru.merkushev.contracts.services;

import ru.merkushev.contracts.account.BankAccountResponse;
import ru.merkushev.contracts.operation.*;

import java.util.List;

public interface OperationService {
    BankAccountResponse deposit(DepositRequest request);

    BankAccountResponse withdraw(WithdrawRequest request);

    TransferResponse transfer(TransferRequest request);

    List<OperationResponse> getOperations(OperationFilterRequest request);

    List<OperationInCurrencyResponse> getOperationsInCurrency(OperationInCurrencyFilterRequest request);
}