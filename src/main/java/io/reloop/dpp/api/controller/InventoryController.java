package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.StockResultDto;
import io.reloop.dpp.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts") // URL métier cohérente
@RequiredArgsConstructor
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
}
