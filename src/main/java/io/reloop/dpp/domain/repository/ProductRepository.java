package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // On écrase la méthode par défaut avec une requête optimisée
    // LEFT JOIN FETCH : "Prends le produit ET ses composants en un seul voyage"
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.components WHERE p.sku = :sku")
    Optional<Product> findBySku(@Param("sku") String sku);
}
