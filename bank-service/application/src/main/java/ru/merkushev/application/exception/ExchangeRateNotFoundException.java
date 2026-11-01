package ru.merkushev.application.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
