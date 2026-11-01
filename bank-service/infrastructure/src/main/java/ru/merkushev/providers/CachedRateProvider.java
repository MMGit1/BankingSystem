package ru.merkushev.providers;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import ru.merkushev.abstractions.providers.ExchangeRateProvider;
import ru.merkushev.application.exception.ExchangeRateNotFoundException;
import ru.merkushev.application.exception.ExchangeRateUnavailableException;
import ru.merkushev.contracts.rates.UpdateExchangeRateRequest;
import ru.merkushev.currencies.Currency;
import ru.merkushev.exchangeRates.ExchangeRate;
import ru.merkushev.messages.RateRequestMessage;
import ru.merkushev.messages.RateResponseMessage;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class CachedRateProvider implements ExchangeRateProvider {
    private final ReplyingKafkaTemplate<String, RateRequestMessage, RateResponseMessage> replyingKafkaTemplate;
    private final ConcurrentHashMap<String, ExchangeRate> cache = new ConcurrentHashMap<>();

    @Value("${app.kafka.topics.rates-requests}")
    private String ratesRequestsTopic;

    @Value("${app.rates.cache-ttl-seconds}")
    private long cacheTtlSeconds;

    @Override
    public ExchangeRate getRate(Currency currency) {
        ExchangeRate cachedRate = cache.get(currency.name());
        if (cachedRate != null
                && cachedRate.getUpdatedAt() != null
                && cachedRate.getUpdatedAt().isAfter(Instant.now().minusSeconds(cacheTtlSeconds))) {
            return cachedRate;
        }

        ProducerRecord<String, RateRequestMessage> record = new ProducerRecord<>(
                ratesRequestsTopic,
                currency.name(),
                new RateRequestMessage(currency)
        );

        try {
            RateResponseMessage response = replyingKafkaTemplate.sendAndReceive(record).get().value();

            if (response.getRateToRub() == null || response.getTimestamp() == null) {
                throw new ExchangeRateNotFoundException(
                        "Exchange rate not found for currency: " + currency
                );
            }

            ExchangeRate freshRate = new ExchangeRate(
                    currency.name(),
                    response.getRateToRub(),
                    response.getTimestamp()
            );

            cache.put(currency.name(), freshRate);
            return freshRate;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExchangeRateUnavailableException("Exchange rate service is unavailable");
        } catch (ExecutionException e) {
            throw new ExchangeRateUnavailableException("Exchange rate service is unavailable");
        }
    }

    @Override
    public void updateRate(UpdateExchangeRateRequest request) {

        cache.put(
                request.getCurrency(),
                new ExchangeRate(
                        request.getCurrency(),
                        request.getRateToRub(),
                        request.getUpdatedAt()
                )
        );
    }
}