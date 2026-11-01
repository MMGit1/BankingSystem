package ru.merkushev.contracts.user;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AddFriendRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID friendId;
}