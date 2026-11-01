package ru.merkushev.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.merkushev.entities.UserEntity;
import ru.merkushev.users.Gender;

public class UserSpecifications {
    public static Specification<UserEntity> hasHairColor(String hairColor) {
        return (root, query, cb) ->
                hairColor == null ? null : cb.equal(root.get("hairColor"), hairColor);
    }

    public static Specification<UserEntity> hasGender(Gender gender) {
        return (root, query, cb) ->
                gender == null ? null : cb.equal(root.get("gender"), gender);
    }
}
