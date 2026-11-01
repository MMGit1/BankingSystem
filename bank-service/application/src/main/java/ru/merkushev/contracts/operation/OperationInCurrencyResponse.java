package ru.merkushev.contracts.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.contracts.enums.OperationTypeDto;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationInCurrencyResponse {
    private UUID id;
    private OperationTypeDto type;
    private UUID accountId;
    private UUID relatedAccountId;
    private BigDecimal amount;
    private BigDecimal commission;
    private String currency;
}