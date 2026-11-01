package ru.merkushev.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.merkushev.application.exception.ExchangeRateNotFoundException;
import ru.merkushev.application.exception.ExchangeRateUnavailableException;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "Entity not found",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ProblemDetail handleExchangeRateNotFound(ExchangeRateNotFoundException e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "Exchange rate not found",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler({
            DomainValidationException.class,
            InsufficientFundsException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ProblemDetail handleBadRequest(Exception e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                message,
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(ExchangeRateUnavailableException.class)
    public ProblemDetail handleExchangeRateUnavailable(ExchangeRateUnavailableException e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Exchange rate service unavailable",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ProblemDetail handleNotImplemented(UnsupportedOperationException e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.NOT_IMPLEMENTED,
                "Not implemented",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception e, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                e.getMessage(),
                request
        );
    }

    private ProblemDetail buildProblemDetail(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}