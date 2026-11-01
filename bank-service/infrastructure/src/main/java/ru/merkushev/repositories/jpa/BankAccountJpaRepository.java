package ru.merkushev.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.merkushev.entities.BankAccountEntity;

import java.util.List;
import java.util.UUID;

public interface BankAccountJpaRepository extends JpaRepository<BankAccountEntity, UUID> {
    List<BankAccountEntity> findAllByOwnerId(UUID ownerId);
}
