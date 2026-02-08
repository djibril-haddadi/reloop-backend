package io.reloop.dpp.domain.service;

import io.reloop.dpp.domain.model.ApiKey;
import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.repository.ApiKeyRepository;
import io.reloop.dpp.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final CompanyRepository companyRepository;

    /**
     * Crée une clé API pour l'entreprise et retourne la clé en clair (à afficher une seule fois).
     */
    @Transactional
    public String createKeyForCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        String plainKey = "reloop_" + randomSegment();
        String keyHash = hashSha256(plainKey);
        ApiKey apiKey = ApiKey.builder()
                .company(company)
                .keyHash(keyHash)
                .enabled(true)
                .build();
        apiKeyRepository.save(apiKey);
        return plainKey;
    }

    private static String randomSegment() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String hashSha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
