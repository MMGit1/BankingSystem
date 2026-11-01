package ru.merkushev.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class User {
    private final UUID id;
    private String passwordHash;
    private Role role;
    private final String login;
    private final String name;
    private int age;
    private final Gender gender;
    private String hairColor;
    private final Set<User> friends;
}
