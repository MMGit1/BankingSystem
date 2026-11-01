package ru.merkushev.contracts.services;

import ru.merkushev.contracts.auth.AuthenticatedUserData;

public interface AuthenticationService {
    AuthenticatedUserData getByLogin(String login);
}
