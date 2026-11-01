package ru.merkushev.application.exception;

public class ExchangeRateUnavailableException extends RuntimeException {
    public ExchangeRateUnavailableException(String message) {
        super(message);
    }
}
