package io.reloop.dpp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. On active le CORS (pour que le mobile/web puisse discuter)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. On désactive le CSRF (inutile pour les API REST)
            .csrf(csrf -> csrf.disable())
            // 3. On autorise tout le monde sur /api/v1/**
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").permitAll() // <-- C'est LA ligne importante
                .anyRequest().permitAll() // Soyons fous : on ouvre tout pour le test
            );

        return http.build();
    }

    // Cette méthode définit les règles CORS "Open Bar"
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Autorise toutes les origines
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
