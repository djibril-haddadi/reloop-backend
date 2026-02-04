package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.ComponentDto;
import io.reloop.dpp.api.dto.ProductDto;
import io.reloop.dpp.domain.model.Product;
import io.reloop.dpp.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/{sku}")
    public ProductDto getProductPassport(@PathVariable String sku) {
        // 1. Récupération du produit via son SKU (Code Unique)
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + sku));

        // 2. Mapping des composants (Entité -> DTO)
        var componentDtos = product.getComponents().stream()
                .map(c -> new ComponentDto(
                        c.getId(),
                        c.getName(),
                        c.getMaterial(),
                        c.getWeightInGrams() // <--- ICI : Mapping explicite weightInGrams -> weight
                ))
                .toList();

        // 3. Construction de la réponse finale
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getReparabilityIndex(),
                componentDtos
        );
    }
}
