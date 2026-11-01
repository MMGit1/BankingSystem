package ru.merkushev.repositories;

import org.springframework.stereotype.Repository;
import ru.merkushev.currencies.Currency;
import ru.merkushev.rates.CurrencyRate;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRateRepository {
    private final Map<String, CurrencyRate> storage = new ConcurrentHashMap<>();

    public void save(CurrencyRate rate) {
        storage.put(rate.getCurrency().toString().toUpperCase(Locale.ROOT), rate);
    }

    public Optional<CurrencyRate> findByCurrency(Currency currency) {
        return Optional.ofNullable(storage.get(currency));
    }
}
