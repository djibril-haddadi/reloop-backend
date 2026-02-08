package io.reloop.dpp.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Réponse sur la racine pour éviter le 404 en ouvrant l'URL Railway dans le navigateur. */
@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> root() {
        return Map.of(
                "message", "Reloop API",
                "docs", "/swagger-ui.html",
                "api", "/api/v1"
        );
    }
}
