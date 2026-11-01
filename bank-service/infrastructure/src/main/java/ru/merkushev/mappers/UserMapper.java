package ru.merkushev.mappers;

import ru.merkushev.entities.UserEntity;
import ru.merkushev.users.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static User mapToDomainWithoutFriends(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getLogin(),
                entity.getName(),
                entity.getAge(),
                entity.getGender(),
                entity.getHairColor(),
                new HashSet<>()
        );
    }

    public static User mapToDomainWithFriends(UserEntity entity) {
        Set<User> friends = entity.getFriends().stream()
                .map(UserMapper::mapToDomainWithoutFriends)
                .collect(Collectors.toSet());

        return new User(
                entity.getId(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getLogin(),
                entity.getName(),
                entity.getAge(),
                entity.getGender(),
                entity.getHairColor(),
                friends
        );
    }

    public static UserEntity mapToEntity(User domain) {
        return new UserEntity(
                domain.getId(),
                domain.getPasswordHash(),
                domain.getRole(),
                domain.getLogin(),
                domain.getName(),
                domain.getAge(),
                domain.getGender(),
                domain.getHairColor(),
                domain.getFriends().stream().map(UserMapper::mapToEntity).collect(Collectors.toSet())
        );
    }
}
