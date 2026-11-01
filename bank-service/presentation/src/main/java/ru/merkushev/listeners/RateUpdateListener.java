package ru.merkushev.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.merkushev.abstractions.providers.ExchangeRateProvider;
import ru.merkushev.contracts.rates.UpdateExchangeRateRequest;
import ru.merkushev.messages.RateUpdateMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateUpdateListener {
    private final ExchangeRateProvider exchangeRateProvider;

    @KafkaListener(
            topics = "${app.kafka.topics.rates-updates}",
            groupId = "bank-service-rates-updates",
            containerFactory = "rateUpdateKafkaListenerContainerFactory"
    )
    public void listen(RateUpdateMessage message) {
        exchangeRateProvider.updateRate(
                new UpdateExchangeRateRequest(
                        message.getCurrency().name(),
                        message.getRateToRub(),
                        message.getTimestamp()
                )
        );

        log.info(
                "Rate update received: currency={}, rateToRub={}, timestamp={}",
                message.getCurrency(),
                message.getRateToRub(),
                message.getTimestamp()
        );
    }
}