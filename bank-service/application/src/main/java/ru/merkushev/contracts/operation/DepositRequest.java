package ru.merkushev.contracts.operation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DepositRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}