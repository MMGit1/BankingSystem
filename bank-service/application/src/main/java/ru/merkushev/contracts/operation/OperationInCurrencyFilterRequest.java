package ru.merkushev.contracts.operation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.contracts.enums.OperationTypeDto;
import ru.merkushev.currencies.Currency;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OperationInCurrencyFilterRequest {
    private UUID accountId;
    private Currency currency;
    private OperationTypeDto type;
}