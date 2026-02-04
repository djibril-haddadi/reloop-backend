package io.reloop.dpp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String API_KEY = "reloop-super-secret-key";
    private static final String API_KEY_HEADER = "x-api-key";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore((request, response, chain) -> {
                    var httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
                    var httpResponse = (jakarta.servlet.http.HttpServletResponse) response;

                    String path = httpRequest.getRequestURI();

                    // 🚨 LA CORRECTION EST ICI 🚨
                    // Si l'URL commence par /swagger-ui ou /v3/api-docs, on laisse passer direct !
                    if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
                        chain.doFilter(request, response);
                        return; // On arrête là pour cette requête, elle est validée
                    }

                    // Pour tout le reste, on vérifie la clé
                    String requestKey = httpRequest.getHeader(API_KEY_HEADER);

                    if (API_KEY.equals(requestKey)) {
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("API_USER", null, java.util.Collections.emptyList())
                        );
                        chain.doFilter(request, response);
                    } else {
                        httpResponse.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                        httpResponse.getWriter().write("Access Denied: Invalid or missing API Key");
                    }
                }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
