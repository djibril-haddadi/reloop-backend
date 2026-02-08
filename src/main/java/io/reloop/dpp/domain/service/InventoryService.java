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

        // Mapping DTO avec garde-fous null
        return inventories.stream()
                .map(inv -> new StockResultDto(
                        inv.getId(),
                        inv.getComponent() != null ? inv.getComponent().getName() : "Inconnu",
                        inv.getComponent() != null ? inv.getComponent().getReference() : "",
                        inv.getCompany() != null ? inv.getCompany().getName() : "Vendeur Inconnu",
                        null,
                        inv.getPriceCents() != null ? inv.getPriceCents() / 100.0 : 0.0,
                        inv.getQuantity(),
                        inv.getCompany() != null && inv.getCompany().getLocation() != null ? inv.getCompany().getLocation().getY() : 0.0,
                        inv.getCompany() != null && inv.getCompany().getLocation() != null ? inv.getCompany().getLocation().getX() : 0.0
                ))
                .toList();
    }
}
