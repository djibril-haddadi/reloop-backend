package io.reloop.dpp.api.dto;

import java.util.UUID;

/**
 * Un item de stock retourné par GET /api/v1/parts/search (pour réserver, afficher liste).
 */
public record StockResultDto(
        UUID inventoryId,
        String componentName,
        String componentRef,
        UUID companyId,
        String companyName,
        double distanceKm,
        double price,
        String conditionCode,
        int quantity,
        boolean available,
        Double latitude,   // position vendeur (carte)
        Double longitude
) {}
