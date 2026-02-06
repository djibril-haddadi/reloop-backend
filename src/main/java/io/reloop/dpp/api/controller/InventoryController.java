package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.model.Inventory;
import io.reloop.dpp.domain.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts") // URL métier cohérente
@RequiredArgsConstructor
@Tag(name = "Parts / Inventory", description = "Recherche de pièces à proximité et ajout de stock")
public class InventoryController {

    private final InventoryService inventoryService;

    // GET /api/v1/parts/search?ref=COMP-PED-500&lat=...&lon=...
    @GetMapping("/search")
    public List<StockResultDto> findPartNearby(
            @RequestParam String ref,     // La référence de la pièce (ex: COMP-PED-500)
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "50") double radius) { // Rayon en Km

        return inventoryService.findStockNearby(ref, lat, lon, radius);
    }

    // Ce code permet à l'app d'envoyer un ordre "Ajoute +500g"
    @PostMapping("/add")
    public ResponseEntity<Inventory> addStock(
            @RequestParam String storeId,
            @RequestParam String materialRef,
            @RequestParam int quantityToAdd) {

        Inventory stock = inventoryService.addStock(storeId, materialRef, quantityToAdd);
        return ResponseEntity.ok(stock);
    }
}
