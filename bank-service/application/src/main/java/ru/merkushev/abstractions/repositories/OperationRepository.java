package ru.merkushev.abstractions.repositories;

import ru.merkushev.operations.Operation;
import ru.merkushev.operations.OperationType;

import java.util.List;
import java.util.UUID;

public interface OperationRepository {
    Operation save(Operation operation);

    List<Operation> findByFilter(UUID accountId, OperationType operationType);
}
