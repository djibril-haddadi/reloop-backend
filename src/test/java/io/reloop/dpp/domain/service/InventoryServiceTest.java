package io.reloop.dpp.domain.service;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.model.Component;
import io.reloop.dpp.domain.model.Inventory;
import io.reloop.dpp.domain.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Active Mockito pour cette classe
class InventoryServiceTest {

    @Mock // Crée une fausse coquille vide du Repository (le Doublure)
    private InventoryRepository inventoryRepository;

    @InjectMocks // Injecte la doublure dans le vrai Service à tester
    private InventoryService inventoryService;

    // Pour créer des faux points GPS
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void findStockNearby_shouldReturnMappedDtos_whenDataExists() {
        // 1. ARRANGE (Préparation du décor)
        // On prépare une fausse entreprise
        Point parisLocation = geometryFactory.createPoint(new Coordinate(2.3522, 48.8566));
        Company fakeCompany = Company.builder()
                .name("Test Shop")
                .type("REPAIR_CENTER")
                .address("1 Rue Test")
                .location(parisLocation)
                .build();

        Component fakeComponent = Component.builder().name("Test Part").reference("REF-123").build();
        Inventory fakeInventory = Inventory.builder()
                .company(fakeCompany)
                .component(fakeComponent)
                .quantity(10)
                .priceCents(5000)
                .conditionCode("USED")
                .available(true)
                .build();

        // On DRESSE la doublure : "Si on t'appelle, réponds ça !"
        when(inventoryRepository.findStockNearby(anyString(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of(fakeInventory));

        // 2. ACT (Action)
        // On appelle la vraie méthode du service
        List<StockResultDto> results = inventoryService.findStockNearby("REF-123", 48.0, 2.0, 10.0);

        // 3. ASSERT (Vérification)
        // On vérifie que le service a bien fait son travail de conversion
        assertEquals(1, results.size());
        assertEquals("Test Shop", results.get(0).companyName());
        assertEquals("Test Part", results.get(0).componentName());
        assertEquals(10, results.get(0).quantity());
        assertEquals(48.8566, results.get(0).latitude());
        assertEquals(50.0, results.get(0).price());
        assertEquals("USED", results.get(0).conditionCode());
        assertEquals(true, results.get(0).available());
    }
}
