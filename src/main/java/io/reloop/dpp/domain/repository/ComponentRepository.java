package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ComponentRepository extends JpaRepository<Component, UUID> {
    // Pas besoin de méthode spécifique pour l'instant, le CRUD de base suffit
}
