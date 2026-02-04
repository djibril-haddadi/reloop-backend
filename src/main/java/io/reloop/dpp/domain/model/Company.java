package io.reloop.dpp.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point; // L'import CRITIQUE pour PostGIS

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @Column(nullable = false)
    private String name;

    // Ex: "RECYCLER", "MANUFACTURER", "REPAIR_CENTER"
    @Column(nullable = false)
    private String type;

    // C'est ici que la magie opère : La colonne spatiale
    // SRID 4326 = Le standard GPS (Google Maps, GPS voiture)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    private String address;

    private String email;
}
