package ru.merkushev.messages;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.currencies.Currency;

import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateUpdateMessage {
    private Currency currency;
    private BigDecimal rateToRub;
    private Instant timestamp;
}
