package ru.merkushev.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.merkushev.entities.OperationEntity;
import ru.merkushev.operations.OperationType;

import java.util.List;
import java.util.UUID;

public interface OperationJpaRepository extends JpaRepository<OperationEntity, UUID> {
    @Query("""
            select o
            from OperationEntity o
            where (:accountId is null or o.accountId = :accountId)
              and (:operationType is null or o.operationType = :operationType)
            """)
    List<OperationEntity> findByFilter(
            @Param("accountId") UUID accountId,
            @Param("operationType") OperationType operationType
    );
}
