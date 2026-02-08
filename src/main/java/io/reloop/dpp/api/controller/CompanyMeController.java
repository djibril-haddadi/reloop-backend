package io.reloop.dpp.api.controller;

import io.reloop.dpp.api.dto.InventoryDto;
import io.reloop.dpp.api.dto.ReservationDto;
import io.reloop.dpp.config.CompanyContextHolder;
import io.reloop.dpp.domain.model.ReservationStatus;
import io.reloop.dpp.domain.repository.ReservationRepository;
import io.reloop.dpp.domain.service.InventoryService;
import io.reloop.dpp.domain.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints "entreprise" : companyId déduit de X-API-KEY (pas dans l'URL).
 */
@RestController
@RequestMapping("/api/v1/company/me")
@RequiredArgsConstructor
@Tag(name = "Company (me)", description = "Réservations et stock de mon entreprise (X-API-KEY requis)")
public class CompanyMeController {

    private final ReservationService reservationService;
    private final InventoryService inventoryService;
    private final ReservationRepository reservationRepository;

    private static UUID requireCompanyId() {
        UUID companyId = CompanyContextHolder.getCompanyId();
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "X-API-KEY required");
        }
        return companyId;
    }

    @Operation(summary = "Mes réservations", description = "Liste les réservations de l'entreprise (filtre status optionnel). Header X-API-KEY obligatoire.")
    @GetMapping("/reservations")
    public List<ReservationDto> listMyReservations(
            @RequestParam(required = false) ReservationStatus status) {
        UUID companyId = requireCompanyId();
        return reservationService.findByCompany(companyId, status);
    }

    @Operation(summary = "Mon stock", description = "Liste les inventaires de l'entreprise. Header X-API-KEY obligatoire.")
    @GetMapping("/inventories")
    public List<InventoryDto> listMyInventories() {
        UUID companyId = requireCompanyId();
        return inventoryService.listByCompany(companyId);
    }

    /** Diagnostic : companyId utilisé + nombre de réservations (pour débug Mode Entreprise). */
    @GetMapping("/info")
    public java.util.Map<String, Object> myCompanyInfo() {
        UUID companyId = requireCompanyId();
        long total = reservationRepository.countByCompany_Id(companyId);
        long pending = reservationRepository.countByCompany_IdAndStatus(companyId, ReservationStatus.PENDING);
        return java.util.Map.of(
                "companyId", companyId.toString(),
                "reservationCount", total,
                "pendingCount", pending
        );
    }
}
