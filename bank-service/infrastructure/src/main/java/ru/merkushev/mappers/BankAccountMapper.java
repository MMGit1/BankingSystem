package ru.merkushev.mappers;

import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.entities.BankAccountEntity;
import ru.merkushev.entities.OperationEntity;

public class BankAccountMapper {
    public static BankAccount mapToDomain(BankAccountEntity entity) {
        return new BankAccount(
                entity.getId(),
                entity.getOwnerId(),
                entity.getBalance()
        );
    }

    public static BankAccountEntity mapToEntity(BankAccount domain) {
        return new BankAccountEntity(
                domain.getId(),
                domain.getOwnerId(),
                domain.getBalance()
        );
    }
}
