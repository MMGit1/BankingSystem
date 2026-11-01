package ru.merkushev.mappers;

import ru.merkushev.entities.OperationEntity;
import ru.merkushev.operations.Operation;

public class OperationMapper {
    public static Operation mapToDomain(OperationEntity entity){
        return new Operation(
                entity.getId(),
                entity.getOperationType(),
                entity.getAccountId(),
                entity.getRelatedAccountId(),
                entity.getAmount(),
                entity.getCommission()
        );
    }

    public static OperationEntity mapToEntity(Operation domain){
        return new OperationEntity(
                domain.getId(),
                domain.getOperationType(),
                domain.getAccountId(),
                domain.getRelatedAccountId(),
                domain.getAmount(),
                domain.getCommission()
        );
    }
}
