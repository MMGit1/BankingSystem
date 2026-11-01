package ru.merkushev.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.merkushev.abstractions.repositories.Repositories;
import ru.merkushev.application.mappers.UserContractMapper;
import ru.merkushev.contracts.services.CurrentUserService;
import ru.merkushev.contracts.user.UserResponse;
import ru.merkushev.exception.EntityNotFoundException;
import ru.merkushev.users.User;

@RequiredArgsConstructor
@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    private final Repositories repositories;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();

        return repositories.getUserRepository()
                .findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    @Override
    public UserResponse getCurrentUserResponse() {
        return UserContractMapper.toResponse(getCurrentUser());
    }
}
