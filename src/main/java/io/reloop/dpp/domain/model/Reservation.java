package io.reloop.dpp.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservations_company_status_created", columnList = "company_id, status, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReservationStatus status;

    /** Ligne d'inventaire exacte (USED vs NEW + prix). Optionnel pour compat. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    /** Condition au moment de la résa (ex: USED, NEW). Snapshot même si inventory_id est renseigné. */
    @Column(name = "condition_code", length = 32)
    private String conditionCode;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "reserved_price_cents", nullable = false)
    private Integer reservedPriceCents;
}
