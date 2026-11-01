package ru.merkushev.repositories.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.merkushev.entities.UserEntity;
import ru.merkushev.users.Gender;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    @EntityGraph(attributePaths = "friends")
    @Query("""
            select u
            from UserEntity u
            where u.id = :id
            """)
    Optional<UserEntity> findByIdWithFriends(@Param("id") UUID id);

    Optional<UserEntity> findByLogin(String login);
}
