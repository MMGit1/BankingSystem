package ru.merkushev.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.contracts.auth.AuthenticatedUserData;
import ru.merkushev.contracts.services.AuthenticationService;
import ru.merkushev.exception.EntityNotFoundException;
import ru.merkushev.users.User;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final Repositories repositories;
    @Override
    public AuthenticatedUserData getByLogin(String login) {
        User user = repositories.getUserRepository()
                .findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new AuthenticatedUserData(
                user.getId(),
                user.getLogin(),
                user.getPasswordHash(),
                user.getRole().name()
        );
    }
}
