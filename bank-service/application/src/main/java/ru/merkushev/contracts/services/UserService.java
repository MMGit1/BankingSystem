package ru.merkushev.contracts.services;

import ru.merkushev.contracts.user.AddFriendRequest;
import ru.merkushev.contracts.user.CreateUserRequest;
import ru.merkushev.contracts.user.UserFilterRequest;
import ru.merkushev.contracts.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID userId);

    void addFriend(AddFriendRequest request);

    void removeFriend(UUID userId, UUID friendId);

    List<UserResponse> getFriends(UUID userId);

    List<UserResponse> getUsers(UserFilterRequest request);
}
