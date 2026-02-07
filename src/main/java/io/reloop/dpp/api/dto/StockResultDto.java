package io.reloop.dpp.api.dto;

import java.util.UUID;

public record StockResultDto(
        UUID id,                 // ✅ AJOUTÉ : Pour identifier la pièce
        String productName,      // ✅ AJOUTÉ : C'est lui qu'il nous manquait ! ("bb")
        String reference,        // ✅ AJOUTÉ : "REF-1234"
        String companyName,
        String address,
        Double price,            // ✅ AJOUTÉ : 50.0
        Integer stockQuantity,
        Double latitude,
        Double longitude
) {}
