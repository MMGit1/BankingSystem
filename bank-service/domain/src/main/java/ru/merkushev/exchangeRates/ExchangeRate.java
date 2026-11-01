package ru.merkushev.exchangeRates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;


@RequiredArgsConstructor
@Getter
public class ExchangeRate {
    private final String currency;
    private final BigDecimal rateToRub;
    private final Instant updatedAt;
}
