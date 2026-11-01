package ru.merkushev.contracts.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.merkushev.contracts.enums.GenderDto;

@Getter
@Setter
@NoArgsConstructor
public class UserFilterRequest {
    private String hairColor;
    private GenderDto gender;
}
