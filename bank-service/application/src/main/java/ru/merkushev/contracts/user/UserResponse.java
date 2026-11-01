package ru.merkushev.contracts.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.merkushev.contracts.enums.GenderDto;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String login;
    private String name;
    private Integer age;
    private GenderDto gender;
    private String hairColor;
}
