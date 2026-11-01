package ru.merkushev.abstractions.providers;

import ru.merkushev.contracts.rates.UpdateExchangeRateRequest;
import ru.merkushev.currencies.Currency;
import ru.merkushev.exchangeRates.ExchangeRate;

public interface ExchangeRateProvider {
    ExchangeRate getRate(Currency currency);

    void updateRate(UpdateExchangeRateRequest request);
}