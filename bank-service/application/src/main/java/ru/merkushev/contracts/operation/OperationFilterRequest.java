package ru.merkushev.contracts.operation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.contracts.enums.OperationTypeDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OperationFilterRequest {
    private UUID accountId;
    private OperationTypeDto type;
}
