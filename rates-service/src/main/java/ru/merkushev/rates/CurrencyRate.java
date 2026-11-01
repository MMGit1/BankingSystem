package ru.merkushev.rates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.currencies.Currency;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class CurrencyRate {
    private Currency currency;
    private BigDecimal rateToRub;
    private Instant updatedAt;
}