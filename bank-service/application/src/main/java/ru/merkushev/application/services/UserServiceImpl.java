package ru.merkushev.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.application.mappers.UserContractMapper;
import ru.merkushev.contracts.services.UserService;
import ru.merkushev.contracts.user.AddFriendRequest;
import ru.merkushev.contracts.user.CreateUserRequest;
import ru.merkushev.contracts.user.UserFilterRequest;
import ru.merkushev.contracts.user.UserResponse;
import ru.merkushev.exception.DomainValidationException;
import ru.merkushev.exception.EntityNotFoundException;
import ru.merkushev.users.Role;
import ru.merkushev.users.User;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final Repositories repositories;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (repositories.getUserRepository().findByLogin(request.getLogin()).isPresent()) {
            throw new DomainValidationException("Login already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = UserContractMapper.toDomain(request, encodedPassword);
        User savedUser = repositories.getUserRepository().save(newUser);

        return UserContractMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = getClientById(userId);
        return UserContractMapper.toResponse(user);
    }

    @Override
    public void addFriend(AddFriendRequest request) {
        User user = repositories.getUserRepository()
                .findByIdWithFriends(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User friend = repositories.getUserRepository()
                .findById(request.getFriendId())
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new DomainValidationException("Admin cannot have friends");
        }

        if (friend.getRole() == Role.ADMIN) {
            throw new DomainValidationException("Admin cannot be added as friend");
        }

        if (user.getId().equals(friend.getId())) {
            throw new DomainValidationException("User cannot add themselves as friend");
        }

        boolean alreadyFriend = user.getFriends().stream()
                .anyMatch(existingFriend -> existingFriend.getId().equals(request.getFriendId()));

        if (!alreadyFriend) {
            user.getFriends().add(friend);
            repositories.getUserRepository().save(user);
        }
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        User user = repositories.getUserRepository()
                .findByIdWithFriends(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new DomainValidationException("Admin cannot have friends");
        }

        boolean removed = user.getFriends().removeIf(friend -> friend.getId().equals(friendId));

        if (!removed) {
            throw new EntityNotFoundException("Friend not found");
        }

        repositories.getUserRepository().save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getFriends(UUID userId) {
        User user = repositories.getUserRepository()
                .findByIdWithFriends(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new DomainValidationException("Admin cannot have friends");
        }

        return user.getFriends().stream()
                .map(UserContractMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers(UserFilterRequest request) {
        return repositories.getUserRepository()
                .findByFilter(
                        request.getHairColor(),
                        UserContractMapper.toDomainGender(request.getGender())
                )
                .stream()
                .filter(user -> user.getRole() == Role.CLIENT)
                .map(UserContractMapper::toResponse)
                .toList();
    }

    private User getClientById(UUID userId) {
        User user = repositories.getUserRepository()
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() != Role.CLIENT) {
            throw new EntityNotFoundException("User not found");
        }

        return user;
    }
}