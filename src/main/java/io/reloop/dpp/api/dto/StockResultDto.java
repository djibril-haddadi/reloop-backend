package io.reloop.dpp.api.dto;

public record StockResultDto(
        String companyName,
        String address,
        Integer stockQuantity,
        Double latitude,
        Double longitude
) {}
