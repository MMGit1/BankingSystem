package ru.merkushev.abstractions.repositories;


import ru.merkushev.users.Gender;
import ru.merkushev.users.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    List<User> findByFilter(String hairColor, Gender gender);

    Optional<User> findByIdWithFriends(UUID id);

    Optional<User> findByLogin(String login);
}
