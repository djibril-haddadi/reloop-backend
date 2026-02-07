package io.reloop.dpp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // On désactive la protection CSRF (inutile pour une API mobile)
            .csrf(csrf -> csrf.disable())
            // On configure les règles d'accès
            .authorizeHttpRequests(auth -> auth
                // ✅ AUTORISER tout ce qui commence par /api/v1/
                .requestMatchers("/api/v1/**").permitAll()
                // 🔒 Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
