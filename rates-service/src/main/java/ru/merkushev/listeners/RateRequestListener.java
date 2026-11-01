package ru.merkushev.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.merkushev.currencies.Currency;
import ru.merkushev.messages.RateRequestMessage;
import ru.merkushev.messages.RateResponseMessage;
import ru.merkushev.rates.CurrencyRate;
import ru.merkushev.repositories.InMemoryRateRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateRequestListener {
    private final InMemoryRateRepository rateRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.rates-requests}",
            groupId = "rates-service-requests",
            containerFactory = "rateRequestKafkaListenerContainerFactory"
    )
    @SendTo("${app.kafka.topics.rates-replies}")
    public RateResponseMessage handle(RateRequestMessage request) {
        Currency currency = request.getCurrency();

        CurrencyRate rate = rateRepository.findByCurrency(currency).orElse(null);

        if (rate == null) {
            log.info("Rate request handled: currency={}, found=false", currency);
            return new RateResponseMessage(currency, null, null);
        }

        log.info("Rate request handled: currency={}, found=true", currency);

        return new RateResponseMessage(
                rate.getCurrency(),
                rate.getRateToRub(),
                rate.getUpdatedAt()
        );
    }
}