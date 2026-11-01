package ru.merkushev.application.mappers;

import ru.merkushev.contracts.enums.OperationTypeDto;
import ru.merkushev.contracts.operation.DepositRequest;
import ru.merkushev.contracts.operation.OperationResponse;
import ru.merkushev.contracts.operation.TransferRequest;
import ru.merkushev.contracts.operation.WithdrawRequest;
import ru.merkushev.operations.Operation;
import ru.merkushev.operations.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

public class OperationContractMapper {
    public static Operation toDepositOperation(DepositRequest request, UUID operationId) {
        return new Operation(
                operationId,
                OperationType.DEPOSIT,
                request.getAccountId(),
                null,
                request.getAmount(),
                BigDecimal.ZERO
        );
    }

    public static Operation toWithdrawOperation(WithdrawRequest request, UUID operationId) {
        return new Operation(
                operationId,
                OperationType.WITHDRAW,
                request.getAccountId(),
                null,
                request.getAmount(),
                BigDecimal.ZERO
        );
    }

    public static Operation toTransferOutOperation(
            TransferRequest request,
            UUID operationId,
            BigDecimal commissionAmount
    ) {
        return new Operation(
                operationId,
                OperationType.TRANSFER_OUT,
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                commissionAmount
        );
    }

    public static Operation toTransferInOperation(
            TransferRequest request,
            UUID operationId
    ) {
        return new Operation(
                operationId,
                OperationType.TRANSFER_IN,
                request.getToAccountId(),
                request.getFromAccountId(),
                request.getAmount(),
                BigDecimal.ZERO
        );
    }

    public static OperationResponse toResponse(Operation operation) {
        return new OperationResponse(
                operation.getId(),
                toDtoType(operation.getOperationType()),
                operation.getAccountId(),
                operation.getRelatedAccountId(),
                operation.getAmount(),
                operation.getCommission()
        );
    }

    public static OperationTypeDto toDtoType(OperationType operationType) {
        return switch (operationType) {
            case DEPOSIT -> OperationTypeDto.DEPOSIT;
            case WITHDRAW -> OperationTypeDto.WITHDRAW;
            case TRANSFER_OUT -> OperationTypeDto.TRANSFER_OUT;
            case TRANSFER_IN -> OperationTypeDto.TRANSFER_IN;
        };
    }

    public static OperationType toDomainType(OperationTypeDto operationTypeDto) {
        if (operationTypeDto == null) {
            return null;
        }

        return switch (operationTypeDto) {
            case DEPOSIT -> OperationType.DEPOSIT;
            case WITHDRAW -> OperationType.WITHDRAW;
            case TRANSFER_OUT -> OperationType.TRANSFER_OUT;
            case TRANSFER_IN -> OperationType.TRANSFER_IN;
        };
    }
}
