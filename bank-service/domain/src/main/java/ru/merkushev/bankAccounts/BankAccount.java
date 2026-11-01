package ru.merkushev.bankAccounts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.exception.DomainValidationException;
import ru.merkushev.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BankAccount {
    private final UUID id;
    private final UUID ownerId;
    private BigDecimal balance;

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
