package ru.merkushev.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.currencies.Currency;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateRequestMessage {
    private Currency currency;
}
