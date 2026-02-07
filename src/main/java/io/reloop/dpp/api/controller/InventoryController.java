package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.model.Component;
import io.reloop.dpp.domain.model.Inventory;
import io.reloop.dpp.domain.repository.CompanyRepository;
import io.reloop.dpp.domain.repository.ComponentRepository;
import io.reloop.dpp.domain.repository.InventoryRepository; // Ajouté pour sauver le stock
import io.reloop.dpp.domain.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parts")
@RequiredArgsConstructor
@Tag(name = "Parts / Inventory", description = "Recherche de pièces à proximité et ajout de stock")
public class InventoryController {

    private final InventoryService inventoryService;

    // ✅ AJOUTS : On injecte les repositories pour faire la sauvegarde directe
    private final InventoryRepository inventoryRepository;
    private final CompanyRepository companyRepository;
    private final ComponentRepository componentRepository;

    @GetMapping("/search")
    public List<StockResultDto> findPartNearby(
            @RequestParam(required = false) String ref, // Le mot clé (ex: "bb")
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "50") double radius) {

        // 🔍 LE MOUCHARD : On affiche ce qu'on reçoit
        if (ref == null || ref.trim().isEmpty()) {
            System.out.println("🔍 Recherche : (VIDE) -> Je renvoie tout.");
            ref = ""; // On s'assure que c'est une chaine vide, pas null
        } else {
            System.out.println("🔍 Recherche : [" + ref + "] -> Je filtre !");
        }

        return inventoryService.findStockNearby(ref, lat, lon, radius);
    }

    // Ajout quantité existante (via Service)
    @PostMapping("/add-quantity")
    public ResponseEntity<Inventory> addStock(
            @RequestParam String storeId,
            @RequestParam String materialRef,
            @RequestParam int quantityToAdd) {

        Inventory stock = inventoryService.addStock(storeId, materialRef, quantityToAdd);
        return ResponseEntity.ok(stock);
    }

    // ✅ MODIFICATION : La vraie sauvegarde
    @PostMapping("/add")
    public ResponseEntity<Inventory> addInventory(@RequestBody CreateInventoryRequest request) {
        System.out.println("💾 Sauvegarde en cours pour : " + request.productName);

        // 1. On trouve le vendeur (le premier de la base, ex: Casse Auto Lille)
        Company seller = companyRepository.findFirstCompany()
                .orElseThrow(() -> new RuntimeException("Aucune entreprise trouvée en base !"));

        // 2. On crée l'objet (Component)
        Component newComponent = new Component();
        newComponent.setName(request.productName);
        newComponent.setReference("REF-" + System.currentTimeMillis()); // Réf unique auto-générée
        newComponent = componentRepository.save(newComponent); // Sauvegarde table components

        // 3. On crée le lien de Stock (Inventory)
        Inventory newStock = new Inventory();
        newStock.setCompany(seller);
        newStock.setComponent(newComponent);
        newStock.setQuantity(1);
        // newStock.setPrice(request.price); // Si tu as un champ prix dans Inventory, décommente ça

        // Sauvegarde finale dans la table inventories
        Inventory savedStock = inventoryRepository.save(newStock);

        System.out.println("✅ Succès ! Stock créé avec l'ID : " + savedStock.getId());
        return ResponseEntity.ok(savedStock);
    }

    // DTO pour le JSON du mobile
    public static class CreateInventoryRequest {
        public String productName;
        public String condition;
        public double price;
        public double lat;
        public double lon;
    }
}
