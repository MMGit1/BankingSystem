package ru.merkushev.abstractions.repositories;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class Repositories {
    private final BankAccountRepository bankAccountRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
}
