package io.reloop.dpp.api.dto;

import java.util.UUID;

// Un objet simple qui porte juste les données, sans logique complexe
public record CompanyDto(
        UUID id,
        String name,
        String type,
        String address,
        Double latitude,  // Plus simple que "Point" pour le front (Google Maps utilise lat/lon)
        Double longitude
) {}
