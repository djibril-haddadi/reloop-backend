package io.reloop.dpp.api.dto;

import java.util.UUID;

/**
 * DTO pour lister les inventaires d'une entreprise (écran Stock / Mode Entreprise).
 */
public record InventoryDto(
        UUID id,
        UUID companyId,
        String componentName,
        int quantity,
        int priceCents,
        double priceEuro,
        String conditionCode,
        boolean available
) {}
