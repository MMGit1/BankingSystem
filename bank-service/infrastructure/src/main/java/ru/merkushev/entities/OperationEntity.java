package ru.merkushev.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.merkushev.operations.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "operations")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OperationEntity {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private UUID accountId;
    private UUID relatedAccountId;
    private BigDecimal amount;
    private BigDecimal commission;
}
