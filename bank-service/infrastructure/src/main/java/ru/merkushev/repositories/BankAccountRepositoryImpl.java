package ru.merkushev.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.merkushev.abstractions.repositories.BankAccountRepository;
import ru.merkushev.bankAccounts.BankAccount;
import ru.merkushev.mappers.BankAccountMapper;
import ru.merkushev.repositories.jpa.BankAccountJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BankAccountRepositoryImpl implements BankAccountRepository {
    private final BankAccountJpaRepository bankAccountJpaRepository;

    @Override
    public BankAccount save(BankAccount account) {
        return BankAccountMapper.mapToDomain(
                bankAccountJpaRepository.save(BankAccountMapper.mapToEntity(account))
        );
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        return bankAccountJpaRepository.findById(id)
                .map(BankAccountMapper::mapToDomain);
    }

    @Override
    public List<BankAccount> findByOwnerId(UUID ownerId) {
        return bankAccountJpaRepository.findAllByOwnerId(ownerId).stream()
                .map(BankAccountMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountJpaRepository.findAll().stream()
                .map(BankAccountMapper::mapToDomain)
                .toList();
    }
}
