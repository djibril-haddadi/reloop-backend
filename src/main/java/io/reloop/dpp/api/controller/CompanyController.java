package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.CompanyDto;
import io.reloop.dpp.api.dto.InventoryDto;
import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.service.ApiKeyService;
import io.reloop.dpp.domain.service.CompanyService;
import io.reloop.dpp.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final InventoryService inventoryService;
    private final ApiKeyService apiKeyService;

    @GetMapping("/nearby")
    public List<CompanyDto> searchNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "50") double radius) {

        // 1. On récupère les entités lourdes de la BDD
        List<Company> companies = companyService.searchNearby(lat, lon, radius);

        // 2. On les convertit en DTO légers (Mapping)
        return companies.stream()
                .map(this::mapToDto)
                .toList();
    }

    // Version "Senior Safe" qui gère les nulls
    private CompanyDto mapToDto(Company c) {
        Double latitude = null;
        Double longitude = null;

        // On vérifie que l'objet géométrique existe avant de demander les coordonnées
        if (c.getLocation() != null) {
            latitude = c.getLocation().getY();
            longitude = c.getLocation().getX();
        }

        return new CompanyDto(
                c.getId(),
                c.getName(),
                c.getType(),
                c.getAddress(),
                latitude,
                longitude
        );
    }

    /** Liste des inventaires (stock) d'une entreprise — écran Mode Entreprise. */
    @GetMapping("/{companyId}/inventories")
    public List<InventoryDto> listInventories(@PathVariable UUID companyId) {
        return inventoryService.listByCompany(companyId);
    }

    /** Crée une clé API pour l'entreprise ; retourne la clé en clair (à sauvegarder, non réaffichée). */
    @PostMapping("/{companyId}/api-keys")
    public java.util.Map<String, String> createApiKey(@PathVariable UUID companyId) {
        String plainKey = apiKeyService.createKeyForCompany(companyId);
        return java.util.Map.of("apiKey", plainKey);
    }
}
