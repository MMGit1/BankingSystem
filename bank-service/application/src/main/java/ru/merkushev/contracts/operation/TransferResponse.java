package ru.merkushev.contracts.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.contracts.account.BankAccountResponse;

@Getter
@AllArgsConstructor
public class TransferResponse {
    private BankAccountResponse fromAccount;
    private BankAccountResponse toAccount;
}
