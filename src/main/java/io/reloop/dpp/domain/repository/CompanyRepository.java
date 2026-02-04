package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    // La requête magique : Trouve tout ce qui est dans un rayon de X mètres
    @Query(value = """
        SELECT * FROM companies c
        WHERE ST_DWithin(
            c.location::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :radiusInMeters
        )
        ORDER BY ST_Distance(
            c.location::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        ) ASC
        """, nativeQuery = true)
    List<Company> findNearbyCompanies(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("radiusInMeters") double radiusInMeters
    );
}
