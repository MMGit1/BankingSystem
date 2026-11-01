package ru.merkushev.contracts.account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {

    @NotNull
    private UUID userId;
}
