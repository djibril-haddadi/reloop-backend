package io.reloop.dpp.domain.service;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.model.Inventory;
import io.reloop.dpp.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory findStock(String storeId, String materialRef) {
        UUID companyId = UUID.fromString(storeId);
        return inventoryRepository.findByCompany_IdAndComponent_Reference(companyId, materialRef)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Stock not found for store " + storeId + " and material " + materialRef));
    }

    @Transactional
    public Inventory addStock(String storeId, String materialRef, int quantityToAdd) {
        Inventory stock = findStock(storeId, materialRef);
        stock.setQuantity(stock.getQuantity() + quantityToAdd);
        return inventoryRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResultDto> findStockNearby(String reference, double lat, double lon, double radiusKm) {
        double radiusMeters = radiusKm * 1000;

        List<Inventory> results = inventoryRepository.findStockNearby(reference, lat, lon, radiusMeters);

        // Conversion (Mapping) Inventory -> DTO
        return results.stream()
                .map(inv -> new StockResultDto(
                        inv.getCompany().getName(),
                        inv.getCompany().getAddress(),
                        inv.getQuantity(),
                        inv.getCompany().getLocation().getY(), // Latitude
                        inv.getCompany().getLocation().getX()  // Longitude
                ))
                .toList();
    }
}
