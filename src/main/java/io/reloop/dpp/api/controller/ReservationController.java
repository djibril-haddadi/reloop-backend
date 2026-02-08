package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.CreateReservationRequest;
import io.reloop.dpp.api.dto.ReservationDto;
import io.reloop.dpp.api.dto.UpdateReservationStatusRequest;
import io.reloop.dpp.domain.model.ReservationStatus;
import io.reloop.dpp.domain.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Création et gestion des réservations (client + entreprise)")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
    summary = "Créer une réservation",
    description = "Request body JSON obligatoire. 400 si body manquant/mal formé ou champs invalides. Exemple : inventoryId (UUID), quantity (≥1), customerName, customerEmail. Stock décrémenté, statut PENDING."
)
    @PostMapping("/reservations")
    public ResponseEntity<ReservationDto> create(@Valid @RequestBody CreateReservationRequest request) {
        ReservationDto created = reservationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Client : mes réservations (par email). Filtre optionnel par statut. Pour l'écran « Mes commandes ». */
    @Operation(summary = "Mes réservations", description = "Liste les réservations du client (customerEmail obligatoire). Option : status pour filtrer.")
    @GetMapping("/reservations")
    public List<ReservationDto> listByCustomerEmail(
            @RequestParam String customerEmail,
            @RequestParam(required = false) ReservationStatus status) {
        return reservationService.findByCustomerEmail(customerEmail, status);
    }

    /** Entreprise : liste les réservations du vendeur, filtre optionnel par statut. */
    @GetMapping("/companies/{companyId}/reservations")
    public List<ReservationDto> listByCompany(
            @PathVariable UUID companyId,
            @RequestParam(required = false) ReservationStatus status) {
        return reservationService.findByCompany(companyId, status);
    }

    /** Entreprise : change le statut (CONFIRMED, READY, CANCELED, PICKED_UP). */
    @PatchMapping("/reservations/{id}/status")
    public ReservationDto updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReservationStatusRequest request) {
        return reservationService.updateStatus(id, request);
    }
}
