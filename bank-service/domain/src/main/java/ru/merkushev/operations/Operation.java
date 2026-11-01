package ru.merkushev.operations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class Operation {
    private final UUID id;
    private final OperationType operationType;
    private final UUID accountId;
    private final UUID relatedAccountId;
    private final BigDecimal amount;
    private final BigDecimal commission;
}
