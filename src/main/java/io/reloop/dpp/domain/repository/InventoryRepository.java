package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    // "Trouve-moi qui a cette pièce en stock (qty > 0) autour de moi"
    @Query(value = """
        SELECT i.* FROM inventories i
        JOIN companies c ON i.company_id = c.id
        JOIN components comp ON i.component_id = comp.id
        WHERE comp.reference = :componentRef
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
