package io.reloop.dpp.api.dto;

import java.util.List;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        String sku,
        Double reparabilityIndex,
        List<ComponentDto> components // La liste imbriquée
) {}
