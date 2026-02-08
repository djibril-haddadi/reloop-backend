package io.reloop.dpp.config;

import io.reloop.dpp.domain.model.Company;
import io.reloop.dpp.domain.repository.ApiKeyRepository;
import io.reloop.dpp.domain.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Pour les requêtes vers /api/v1/company/me/** : déduit companyId depuis X-API-KEY (ou, si un seul magasin, celui-ci).
 */
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String PATH_PREFIX = "/api/v1/company/me/";

    private final ApiKeyRepository apiKeyRepository;
    private final CompanyRepository companyRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith(PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String apiKey = request.getHeader(HEADER_API_KEY);
            UUID companyId = null;

            if (apiKey != null && !apiKey.isBlank()) {
                String keyHash = hashSha256(apiKey.trim());
                companyId = apiKeyRepository.findCompanyIdByKeyHashAndEnabledTrue(keyHash).orElse(null);
            }

            // Fallback : un seul magasin en base → on l'utilise (évite clé / mauvaise clé).
            if (companyId == null && companyRepository.count() == 1) {
                companyId = companyRepository.findFirstCompany()
                        .map(Company::getId)
                        .orElse(null);
            }

            if (companyId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Missing or invalid X-API-KEY (or several companies: clé requise)\"}");
                return;
            }

            CompanyContextHolder.setCompanyId(companyId);
            filterChain.doFilter(request, response);
        } finally {
            CompanyContextHolder.clear();
        }
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
