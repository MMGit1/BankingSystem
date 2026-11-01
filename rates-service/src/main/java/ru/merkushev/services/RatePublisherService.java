package ru.merkushev.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.merkushev.currencies.Currency;
import ru.merkushev.messages.RateUpdateMessage;
import ru.merkushev.rates.CurrencyRate;
import ru.merkushev.repositories.InMemoryRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatePublisherService {
    private final KafkaTemplate<String, RateUpdateMessage> rateUpdateKafkaTemplate;
    private final InMemoryRateRepository rateRepository;

    @Value("${app.kafka.topics.rates-updates}")
    private String ratesUpdatesTopic;

    private final Random random = new Random();

    @Scheduled(fixedDelayString = "${app.rates.publish-period-ms}")
    public void publishRates() {
        publishSingle(Currency.USD, new BigDecimal("90.00"), new BigDecimal("2.00"));
        publishSingle(Currency.EUR, new BigDecimal("100.00"), new BigDecimal("2.50"));
    }

    public void publishSingle(Currency currency, BigDecimal initialValue, BigDecimal delta) {
        BigDecimal currentValue = rateRepository.findByCurrency(currency)
                .map(CurrencyRate::getRateToRub)
                .orElse(initialValue);

        double shift = (random.nextDouble() * 2.0 - 1.0) * delta.doubleValue();

        BigDecimal nextValue = currentValue
                .add(BigDecimal.valueOf(shift))
                .setScale(2, RoundingMode.HALF_UP);

        if (nextValue.signum() < 1) {
            nextValue = BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP);
        }

        CurrencyRate rate = new CurrencyRate(currency, nextValue, Instant.now());
        rateRepository.save(rate);

        RateUpdateMessage message = new RateUpdateMessage(
                rate.getCurrency(),
                rate.getRateToRub(),
                rate.getUpdatedAt()
        );

        rateUpdateKafkaTemplate.send(ratesUpdatesTopic, currency.name(), message);

        log.info(
                "Rate published: currency={}, rateToRub={}, timestamp={}",
                message.getCurrency(),
                message.getRateToRub(),
                message.getTimestamp()
        );
    }
}