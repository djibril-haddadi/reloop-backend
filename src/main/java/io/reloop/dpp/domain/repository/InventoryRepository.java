package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByCompany_IdAndComponent_Reference(UUID companyId, String componentReference);

    // ✅ REQUÊTE CORRIGÉE ET "BLINDÉE" :
    // Elle cherche maintenant dans le NOM (name) ET la RÉFÉRENCE (reference)
    @Query(value = """
        SELECT i.* FROM inventories i
        JOIN companies c ON i.company_id = c.id
        JOIN components comp ON i.component_id = comp.id
        WHERE 
            i.quantity > 0
            AND (
                -- 1. Si la recherche est vide ou nulle, on renvoie tout
                :componentRef IS NULL 
                OR :componentRef = '' 
                -- 2. Sinon, on cherche si ça matche le NOM OU la RÉFÉRENCE
                OR comp.name ILIKE CONCAT('%', :componentRef, '%')       -- <--- C'EST ICI LA CLÉ (pour trouver "Moteur")
                OR comp.reference ILIKE CONCAT('%', :componentRef, '%')  -- (pour trouver "REF-123")
            )
            AND ST_DWithin(
                c.location::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radiusInMeters
            )
        ORDER BY ST_Distance(
            c.location::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        ) ASC
        """, nativeQuery = true)
    List<Inventory> findStockNearby(
            @Param("componentRef") String componentRef,
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radiusInMeters") double radiusInMeters
    );
}
