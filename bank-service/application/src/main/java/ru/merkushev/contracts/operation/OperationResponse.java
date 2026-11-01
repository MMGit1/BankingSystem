package ru.merkushev.contracts.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.contracts.enums.OperationTypeDto;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OperationResponse {
    private UUID id;
    private OperationTypeDto type;
    private UUID accountId;
    private UUID relatedAccountId;
    private BigDecimal amount;
    private BigDecimal commission;
}
