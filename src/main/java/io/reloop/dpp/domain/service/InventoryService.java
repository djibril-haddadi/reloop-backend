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
        int newQty = stock.getQuantity() + quantityToAdd;
        stock.setQuantity(newQty);
        stock.setAvailable(newQty > 0); // cohérence : disponible ssi quantité > 0
        return inventoryRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResultDto> findStockNearby(String ref, double lat, double lon, double radius) {

        // 🧠 LOGIQUE JAVA (Plus fiable que SQL) :
        String searchPattern;
        if (ref == null || ref.trim().isEmpty()) {
            searchPattern = "%"; // Le joker : "Je veux tout"
        } else {
            searchPattern = "%" + ref.trim() + "%"; // "Je veux ce qui contient ce mot"
        }

        List<Inventory> inventories = inventoryRepository.findStockNearby(searchPattern, lat, lon, radius * 1000);

        return inventories.stream()
                .map(inv -> toStockResultDto(inv, lat, lon))
                .toList();
    }

    private static StockResultDto toStockResultDto(Inventory inv, double searchLat, double searchLon) {
        String componentName = inv.getComponent() != null ? inv.getComponent().getName() : "Inconnu";
        String componentRef = inv.getComponent() != null ? inv.getComponent().getReference() : "";
        UUID companyId = inv.getCompany() != null ? inv.getCompany().getId() : null;
        String companyName = inv.getCompany() != null ? inv.getCompany().getName() : "Vendeur Inconnu";
        double distanceKm = 0.0;
        if (inv.getCompany() != null && inv.getCompany().getLocation() != null) {
            double companyLat = inv.getCompany().getLocation().getY();
            double companyLon = inv.getCompany().getLocation().getX();
            distanceKm = distanceKm(searchLat, searchLon, companyLat, companyLon);
        }
        double price = inv.getPriceCents() != null ? inv.getPriceCents() / 100.0 : 0.0;
        String conditionCode = inv.getConditionCode() != null ? inv.getConditionCode() : "USED";
        int quantity = inv.getQuantity() != null ? inv.getQuantity() : 0;
        boolean available = Boolean.TRUE.equals(inv.getAvailable());
        Double latitude = null;
        Double longitude = null;
        if (inv.getCompany() != null && inv.getCompany().getLocation() != null) {
            latitude = inv.getCompany().getLocation().getY();
            longitude = inv.getCompany().getLocation().getX();
        }

        return new StockResultDto(
                inv.getId(),
                componentName,
                componentRef,
                companyId,
                companyName,
                distanceKm,
                price,
                conditionCode,
                quantity,
                available,
                latitude,
                longitude
        );
    }

    /** Distance en km (Haversine) entre deux points WGS84. */
    private static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // rayon Terre en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
