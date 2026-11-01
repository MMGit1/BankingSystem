package ru.merkushev.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.merkushev.abstractions.repositories.UserRepository;
import ru.merkushev.entities.UserEntity;
import ru.merkushev.mappers.UserMapper;
import ru.merkushev.repositories.jpa.UserJpaRepository;
import ru.merkushev.specifications.UserSpecifications;
import ru.merkushev.users.Gender;
import ru.merkushev.users.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return UserMapper.mapToDomainWithoutFriends(
                userJpaRepository.save(UserMapper.mapToEntity(user))
        );
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::mapToDomainWithoutFriends);
    }

    @Override
    public Optional<User> findByIdWithFriends(UUID id) {
        return userJpaRepository.findByIdWithFriends(id)
                .map(UserMapper::mapToDomainWithFriends);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userJpaRepository.findByLogin(login)
                .map(UserMapper::mapToDomainWithoutFriends);
    }

    @Override
    public List<User> findByFilter(String hairColor, Gender gender) {
        Specification<UserEntity> specification = Specification
                .where(UserSpecifications.hasHairColor(hairColor))
                .and(UserSpecifications.hasGender(gender));

        return userJpaRepository.findAll(specification).stream()
                .map(UserMapper::mapToDomainWithoutFriends)
                .toList();
    }
}
