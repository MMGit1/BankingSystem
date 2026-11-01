package ru.merkushev.abstractions.repositories;

import ru.merkushev.bankAccounts.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository {
    BankAccount save(BankAccount account);

    Optional<BankAccount> findById(UUID id);

    List<BankAccount> findByOwnerId(UUID ownerId);

    List<BankAccount> findAll();
}
