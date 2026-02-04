package io.reloop.dpp.api.dto;

import java.util.UUID;

public record ComponentDto(
        UUID id,
        String name,
        String material,
        Double weight // On mappe weightInGrams ici
) {}
