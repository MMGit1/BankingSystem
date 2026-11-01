package ru.merkushev.contracts.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.currencies.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class BalanceInCurrencyResponse {
    private UUID accountId;
    private BigDecimal balance;
    private Currency currency;
}