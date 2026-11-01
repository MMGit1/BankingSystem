package ru.merkushev.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.merkushev.users.Gender;
import ru.merkushev.users.Role;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {
    @Id
    private UUID id;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String login;
    private String name;
    private int age;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String hairColor;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<UserEntity> friends;
}
