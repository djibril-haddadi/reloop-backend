package io.reloop.dpp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "ReLoop Enterprise API", version = "1.0", description = "API de gestion du Passeport Numérique Produit (DPP)"),
        // On applique la sécurité par défaut à TOUTES les routes
        security = @SecurityRequirement(name = "ApiKeyAuth")
)
@SecurityScheme(
        name = "ApiKeyAuth",        // Le nom interne de la sécurité
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "x-api-key"     // ⚠️ Le nom EXACT du header HTTP attendu
)
public class OpenApiConfig {
    // Classe de configuration vide, les annotations font tout le travail !
}
