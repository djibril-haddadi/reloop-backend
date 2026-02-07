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

    // GET /api/v1/parts/search?ref=COMP-PED-500&lat=...&lon=... (ref optionnel)
    @GetMapping("/search")
    public List<StockResultDto> findPartNearby(
            @RequestParam(required = false) String ref,     // La référence de la pièce (ex: COMP-PED-500)
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "50") double radius) { // Rayon en Km

        if (ref == null) ref = "";
        return inventoryService.findStockNearby(ref, lat, lon, radius);
    }

    // Ce code permet à l'app d'envoyer un ordre "Ajoute +500g"
    @PostMapping("/add-quantity")
    public ResponseEntity<Inventory> addStock(
            @RequestParam String storeId,
            @RequestParam String materialRef,
            @RequestParam int quantityToAdd) {

        Inventory stock = inventoryService.addStock(storeId, materialRef, quantityToAdd);
        return ResponseEntity.ok(stock);
    }

    // Endpoint pour ajouter une pièce au stock
    @PostMapping("/add")
    public ResponseEntity<Inventory> addInventory(@RequestBody CreateInventoryRequest request) {
        // C'est ici qu'on appellerait le Service pour sauver en base
        // Pour l'instant, on va juste logger pour voir si ça marche
        System.out.println("📦 Ajout de stock reçu : " + request.productName + " (" + request.condition + ")");

        // On renvoie un succès vide pour le test
        return ResponseEntity.ok().build();
    }

    // Petite classe (DTO) pour transporter les infos du mobile vers le serveur
    public static class CreateInventoryRequest {
        public String productName;
        public String condition; // "NEUF" ou "OCCASION"
        public double price;
        public double lat;
        public double lon;
    }
}
