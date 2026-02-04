package io.reloop.dpp.domain.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Design Pattern Builder pour créer des objets proprement
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku; // Stock Keeping Unit (référence unique Decathlon/Marque)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reparability_index")
    private Double reparabilityIndex; // Score de 0.0 à 10.0

    // Exemple : "BICYCLE", "ELECTRONICS"
    @Column(nullable = false)
    private String category;

    // RELATION MANY-TO-MANY
    // Un produit a plusieurs composants
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_components", // Nom de la table de jointure (créée auto)
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "component_id")
    )
    private Set<Component> components = new HashSet<>();
}
