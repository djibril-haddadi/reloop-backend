package io.reloop.dpp.domain.service;

import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Génère le constructeur pour l'injection de dépendances
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<Company> searchNearby(double lat, double lon, double radiusKm) {
        // Conversion Km -> Mètres pour PostGIS
        double radiusMeters = radiusKm * 1000;
        return companyRepository.findNearbyCompanies(lon, lat, radiusMeters);
    }
}
