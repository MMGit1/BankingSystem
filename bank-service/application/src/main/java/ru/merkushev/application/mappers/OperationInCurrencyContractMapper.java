package ru.merkushev.application.mappers;

import ru.merkushev.contracts.enums.OperationTypeDto;
import ru.merkushev.contracts.operation.OperationInCurrencyResponse;
import ru.merkushev.operations.Operation;

import java.math.BigDecimal;

public class OperationInCurrencyContractMapper {
    public static OperationInCurrencyResponse toResponse(
            Operation operation,
            BigDecimal amount,
            BigDecimal commission,
            String currency
    ) {
        return new OperationInCurrencyResponse(
                operation.getId(),
                OperationTypeDto.valueOf(operation.getOperationType().name()),
                operation.getAccountId(),
                operation.getRelatedAccountId(),
                amount,
                commission,
                currency
        );
    }
}