package io.reloop.dpp.domain.repository;

import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.model.Component;
import io.reloop.dpp.domain.model.Inventory;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers // Active la magie Docker
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventoryRepositoryTest {

    // On définit l'image Docker exacte qu'on utilise en prod (PostGIS 16)
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgisContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres"));

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Test
    void findStockNearby_shouldReturnOnlyLocalStock() {
        // --- 1. SETUP (Préparation des données) ---
        GeometryFactory gf = new GeometryFactory();

        // A. Création du Composant (Pédale)
        Component pedal = Component.builder()
                .name("Pedal Grip 500")
                .reference("REF-TEST-PEDAL") // La ref qu'on va chercher
                .weightInGrams(150.0)
                .build();
        componentRepository.save(pedal);

        // B. Création Boutique PARIS (GPS Tour Eiffel)
        Point parisLoc = gf.createPoint(new Coordinate(2.2945, 48.8584));
        parisLoc.setSRID(4326); // Indispensable pour PostGIS !

        Company parisShop = Company.builder()
                .name("Paris Shop")
                .type("REPAIR")
                .location(parisLoc)
                .build();
        companyRepository.save(parisShop);

        // C. Création Boutique LYON (Loin !)
        Point lyonLoc = gf.createPoint(new Coordinate(4.8357, 45.7640));
        lyonLoc.setSRID(4326);

        Company lyonShop = Company.builder()
                .name("Lyon Shop")
                .type("REPAIR")
                .location(lyonLoc)
                .build();
        companyRepository.save(lyonShop);

        // D. On met du stock dans les deux boutiques
        Inventory stockParis = Inventory.builder().company(parisShop).component(pedal).quantity(5).build();
        Inventory stockLyon = Inventory.builder().company(lyonShop).component(pedal).quantity(10).build();

        inventoryRepository.save(stockParis);
        inventoryRepository.save(stockLyon);

        // --- 2. EXECUTION ---
        // On cherche à 10km autour de Paris
        List<Inventory> results = inventoryRepository.findStockNearby(
                "REF-TEST-PEDAL",
                48.8584, // Lat Paris
                2.2945,  // Lon Paris
                10000    // 10km en mètres
        );

        // --- 3. VERIFICATION ---
        assertThat(results).hasSize(1); // On ne doit trouver qu'UNE seule boutique
        assertThat(results.get(0).getCompany().getName()).isEqualTo("Paris Shop"); // Et c'est celle de Paris
        assertThat(results.get(0).getQuantity()).isEqualTo(5);
    }
}
