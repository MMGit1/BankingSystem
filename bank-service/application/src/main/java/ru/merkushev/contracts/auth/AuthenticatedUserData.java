package ru.merkushev.contracts.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthenticatedUserData {
    private UUID id;
    private String login;
    private String passwordHash;
    private String role;
}