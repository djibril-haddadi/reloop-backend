package io.reloop.dpp.domain.service;

import io.reloop.dpp.api.dto.CreateReservationRequest;
import io.reloop.dpp.api.dto.ReservationDto;
import io.reloop.dpp.api.dto.UpdateReservationStatusRequest;
import io.reloop.dpp.domain.model.*;
import io.reloop.dpp.domain.repository.InventoryRepository;
import io.reloop.dpp.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Crée une réservation : snap du prix, statut PENDING, et décrémente le stock pour éviter la survente.
     */
    @Transactional
    public ReservationDto create(CreateReservationRequest request) {
        if (reservationRepository.existsByInventory_IdAndCustomerEmailIgnoreCaseAndStatus(
                request.getInventoryId(), request.getCustomerEmail(), ReservationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Une réservation PENDING existe déjà pour ce stock et cet email.");
        }

        Inventory inventory = inventoryRepository.findById(request.getInventoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found: " + request.getInventoryId()));

        if (!Boolean.TRUE.equals(inventory.getAvailable())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article non disponible à la vente.");
        }

        int available = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
        if (available < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuffisant: disponible=" + available + ", demandé=" + request.getQuantity());
        }

        int newQty = available - request.getQuantity();
        inventory.setQuantity(newQty);
        inventory.setAvailable(newQty > 0);
        inventoryRepository.save(inventory);

        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.PENDING)
                .inventory(inventory)
                .company(inventory.getCompany())
                .component(inventory.getComponent())
                .conditionCode(inventory.getConditionCode())
                .quantity(request.getQuantity())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .reservedPriceCents(inventory.getPriceCents() != null ? inventory.getPriceCents() : 0)
                .build();

        reservation = reservationRepository.save(reservation);
        return toDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> findByCompany(UUID companyId, ReservationStatus statusFilter) {
        List<Reservation> list = statusFilter == null
                ? reservationRepository.findByCompany_IdOrderByCreatedAtDesc(companyId)
                : reservationRepository.findByCompany_IdAndStatusOrderByCreatedAtDesc(companyId, statusFilter);
        return list.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> findByCustomerEmail(String customerEmail, ReservationStatus statusFilter) {
        if (customerEmail == null || customerEmail.isBlank()) {
            return List.of();
        }
        String email = customerEmail.trim();
        List<Reservation> list = statusFilter == null
                ? reservationRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email)
                : reservationRepository.findByCustomerEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(email, statusFilter);
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public ReservationDto updateStatus(UUID id, UpdateReservationStatusRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found: " + id));

        ReservationStatus from = reservation.getStatus();
        ReservationStatus to = request.getStatus();

        if (from == to) {
            return toDto(reservation);
        }

        if (!isAllowedTransition(from, to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transition non autorisée: " + from + " → " + to);
        }

        if (to == ReservationStatus.CANCELED && from != ReservationStatus.CANCELED && from != ReservationStatus.PICKED_UP) {
            Inventory inventory = reservation.getInventory();
            if (inventory != null && reservation.getQuantity() != null) {
                int currentQty = inventory.getQuantity() != null ? inventory.getQuantity() : 0;
                int addBack = reservation.getQuantity();
                int newQty = currentQty + addBack;
                inventory.setQuantity(newQty);
                inventory.setAvailable(newQty > 0);
                inventoryRepository.save(inventory);
            }
        }

        reservation.setStatus(to);
        reservation = reservationRepository.save(reservation);
        return toDto(reservation);
    }

    /** MVP : PENDING→CONFIRMED|CANCELED, CONFIRMED→READY|CANCELED, READY→PICKED_UP|CANCELED ; PICKED_UP/CANCELED = finaux. */
    private boolean isAllowedTransition(ReservationStatus from, ReservationStatus to) {
        return switch (from) {
            case PENDING -> to == ReservationStatus.CONFIRMED || to == ReservationStatus.CANCELED;
            case CONFIRMED -> to == ReservationStatus.READY || to == ReservationStatus.CANCELED;
            case READY -> to == ReservationStatus.PICKED_UP || to == ReservationStatus.CANCELED;
            case PICKED_UP, CANCELED -> false;
        };
    }

    private ReservationDto toDto(Reservation r) {
        int cents = r.getReservedPriceCents() != null ? r.getReservedPriceCents() : 0;
        return new ReservationDto(
                r.getId(),
                r.getStatus(),
                r.getInventory() != null ? r.getInventory().getId() : null,
                r.getCompany() != null ? r.getCompany().getId() : null,
                r.getCompany() != null ? r.getCompany().getName() : "",
                r.getComponent() != null ? r.getComponent().getName() : "",
                r.getQuantity() != null ? r.getQuantity() : 0,
                r.getCustomerName(),
                r.getCustomerEmail(),
                cents,
                cents / 100.0,
                r.getConditionCode() != null ? r.getConditionCode() : "USED",
                r.getCreatedAt()
        );
    }
}
