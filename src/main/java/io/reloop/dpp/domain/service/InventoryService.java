package io.reloop.dpp.domain.service;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.model.Inventory;
import io.reloop.dpp.domain.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

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
