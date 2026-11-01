package ru.merkushev.contracts.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BalanceResponse {
    private UUID accountId;
    private BigDecimal balance;
}
