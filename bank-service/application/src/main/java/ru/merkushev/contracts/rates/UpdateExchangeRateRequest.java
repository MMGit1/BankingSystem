package ru.merkushev.contracts.rates;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Getter
public class UpdateExchangeRateRequest {
    private String currency;
    private BigDecimal rateToRub;
    private Instant updatedAt;
}
