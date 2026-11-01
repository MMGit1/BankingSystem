package ru.merkushev.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.merkushev.abstractions.repositories.OperationRepository;
import ru.merkushev.mappers.OperationMapper;
import ru.merkushev.operations.Operation;
import ru.merkushev.operations.OperationType;
import ru.merkushev.repositories.jpa.OperationJpaRepository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OperationRepositoryImpl implements OperationRepository {
    private final OperationJpaRepository operationJpaRepository;

    @Override
    public Operation save(Operation operation) {
        return OperationMapper.mapToDomain(
                operationJpaRepository.save(OperationMapper.mapToEntity(operation))
        );
    }

    @Override
    public List<Operation> findByFilter(UUID accountId, OperationType operationType) {
        return operationJpaRepository.findByFilter(accountId, operationType).stream()
                .map(OperationMapper::mapToDomain)
                .toList();
    }
}