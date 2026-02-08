package io.reloop.dpp.api.dto;

import io.reloop.dpp.domain.model.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReservationStatusRequest {

    @NotNull
    private ReservationStatus status;
}
