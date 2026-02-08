package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.ApiKey;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    Optional<ApiKey> findByKeyHashAndEnabledTrue(String keyHash);

    /** Évite LazyInit dans le filtre (pas de session). */
    @Query("SELECT a.company.id FROM ApiKey a WHERE a.keyHash = :keyHash AND a.enabled = true")
    Optional<UUID> findCompanyIdByKeyHashAndEnabledTrue(@Param("keyHash") String keyHash);
}
