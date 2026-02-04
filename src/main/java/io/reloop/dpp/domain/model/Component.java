package io.reloop.dpp.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Component extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String reference; // Référence fournisseur

    private String material; // Ex: "ALUMINIUM_6061", "RECYCLED_PLASTIC"

    private Double weightInGrams;
}
