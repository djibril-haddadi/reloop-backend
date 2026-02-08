package io.reloop.dpp.api.dto;

import io.reloop.dpp.domain.model.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public record ReservationDto(
        UUID id,
        ReservationStatus status,
        UUID inventoryId,
        UUID companyId,
        String companyName,
        String componentName,
        int quantity,
        String customerName,
        String customerEmail,
        int reservedPriceCents,
        double reservedPriceEuro,
        String conditionCode,
        Instant createdAt
) {}
