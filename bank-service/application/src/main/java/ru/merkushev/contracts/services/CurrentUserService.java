package ru.merkushev.contracts.services;

import ru.merkushev.contracts.user.UserResponse;
import ru.merkushev.users.User;

public interface CurrentUserService {
    User getCurrentUser();
    UserResponse getCurrentUserResponse();
}
