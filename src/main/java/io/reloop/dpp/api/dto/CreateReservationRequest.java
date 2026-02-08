package io.reloop.dpp.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
    description = "Création d'une réservation (client). Body JSON obligatoire.",
    example = "{\"inventoryId\":\"fd3b6e49-7410-483c-bd5a-b22bba18548b\",\"quantity\":1,\"customerName\":\"Djib\",\"customerEmail\":\"djib@test.com\"}"
)
public class CreateReservationRequest {

    @NotNull
    @Schema(description = "ID de la ligne d'inventaire (stock) à réserver", example = "fd3b6e49-7410-483c-bd5a-b22bba18548b", required = true)
    private UUID inventoryId;

    @Min(1)
    @Schema(description = "Quantité à réserver", example = "1", defaultValue = "1")
    private int quantity = 1;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Nom du client", example = "Djib", required = true)
    private String customerName;

    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(description = "Email du client", example = "djib@test.com", required = true)
    private String customerEmail;
}
