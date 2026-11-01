package ru.merkushev.application.mappers;

import ru.merkushev.contracts.enums.GenderDto;
import ru.merkushev.contracts.enums.RoleDto;
import ru.merkushev.contracts.user.CreateUserRequest;
import ru.merkushev.contracts.user.UserResponse;
import ru.merkushev.users.Gender;
import ru.merkushev.users.Role;
import ru.merkushev.users.User;

import java.util.HashSet;
import java.util.UUID;

public class UserContractMapper {
    public static User toDomain(CreateUserRequest request, String encodedPassword) {
        return new User(
                UUID.randomUUID(),
                encodedPassword,
                toDomainRole(request.getRole()),
                request.getLogin(),
                request.getName(),
                request.getAge(),
                toDomainGender(request.getGender()),
                request.getHairColor(),
                new HashSet<>()
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getAge(),
                toDtoGender(user.getGender()),
                user.getHairColor()
        );
    }

    public static Gender toDomainGender(GenderDto genderDto) {
        if (genderDto == null) {
            return null;
        }

        return switch (genderDto) {
            case MEN -> Gender.MEN;
            case WOMEN -> Gender.WOMEN;
        };
    }

    public static GenderDto toDtoGender(Gender gender) {
        if (gender == null) {
            return null;
        }

        return switch (gender) {
            case MEN -> GenderDto.MEN;
            case WOMEN -> GenderDto.WOMEN;
        };
    }

    public static Role toDomainRole(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }

        return switch (roleDto) {
            case ADMIN -> Role.ADMIN;
            case CLIENT -> Role.CLIENT;
        };
    }
}
