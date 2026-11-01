package ru.merkushev.contracts.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.merkushev.contracts.enums.GenderDto;
import ru.merkushev.contracts.enums.RoleDto;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String password;

    @NotBlank
    private String login;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer age;

    @NotNull
    private GenderDto gender;

    @NotBlank
    private String hairColor;

    @NotNull
    private RoleDto role;
}
