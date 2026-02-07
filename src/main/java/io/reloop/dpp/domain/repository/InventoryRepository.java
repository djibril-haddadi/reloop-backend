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

    // ✅ REQUÊTE CORRIGÉE :
    // 1. Gère le cas où componentRef est NULL ou VIDE ('') -> Renvoie tout
    // 2. Utilise ILIKE pour que "moteur" trouve "MOTEUR" (insensible à la casse)
    @Query(value = """
        SELECT i.* FROM inventories i
        JOIN companies c ON i.company_id = c.id
        JOIN components comp ON i.component_id = comp.id
        WHERE 
            ( :componentRef IS NULL OR :componentRef = '' OR comp.reference ILIKE CONCAT('%', :componentRef, '%') )
        AND i.quantity > 0
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
