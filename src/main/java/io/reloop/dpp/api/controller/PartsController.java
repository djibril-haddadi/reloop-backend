package io.reloop.dpp.api.controller;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@CrossOrigin(origins = "*") // On ouvre les vannes pour le mobile
public class PartsController {

    // L'endpoint que l'app mobile appelle
    @GetMapping("/search")
    public List<PartLocation> search(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "500") double radius, // Rayon par défaut
            @RequestParam(required = false) String ref) {      // Ref est optionnel !

        System.out.println("📡 Recherche reçue : Lat=" + lat + " Lon=" + lon);

        // --- MOCK DATA (Fausses données pour le test) ---
        List<PartLocation> fakeParts = new ArrayList<>();

        // 1. Une pièce juste à côté de toi (Lille)
        fakeParts.add(new PartLocation("Moteur V8", lat + 0.001, lon + 0.001));

        // 2. Une autre pièce un peu plus loin
        fakeParts.add(new PartLocation("Pare-choc", lat - 0.002, lon - 0.002));
        
        // 3. Une troisième
        fakeParts.add(new PartLocation("Rétroviseur", lat + 0.003, lon - 0.001));

        // 4. Une pièce à Paris (pour vérifier le dézoom)
        fakeParts.add(new PartLocation("Boite de vitesse", 48.8566, 2.3522));

        return fakeParts;
    }

    // Petite classe interne pour définir à quoi ressemble la réponse JSON
    // C'est ce que Flutter va lire (latitude, longitude)
    static class PartLocation {
        public String name;
        public double latitude;
        public double longitude;

        public PartLocation(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
