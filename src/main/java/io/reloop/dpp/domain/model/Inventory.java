package io.reloop.dpp.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories",
       uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "component_id", "condition_code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @Column(nullable = false)
    private Integer quantity;

    /** Prix en centimes (ex: 5000 = 50,00 €). */
    @Column(name = "price_cents", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 5000")
    private Integer priceCents = 5000;

    /** NEW, USED, REFURB, etc. */
    @Column(name = "condition_code", nullable = false, length = 32, columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'USED'")
    private String conditionCode = "USED";

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT true")
    private Boolean available = true;
}
