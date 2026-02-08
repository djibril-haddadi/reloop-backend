package io.reloop.dpp.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateReservationRequest {

    @NotNull
    private UUID inventoryId;

    @Min(1)
    private int quantity = 1;

    @NotBlank
    @Size(max = 255)
    private String customerName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String customerEmail;
}
