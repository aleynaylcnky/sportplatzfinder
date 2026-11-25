package de.sportplatzfinder.backend.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import de.sportplatzfinder.backend.model.Benutzer;
import de.sportplatzfinder.backend.repository.BenutzerRepository;

/**
 * Ermittelt den eingeloggten Benutzer anhand der Keycloak-ID (JWT "sub").
 * Existiert noch kein Eintrag in der Tabelle benutzer, wird er beim ersten Zugriff angelegt.
 */
@Component
public class CurrentUserResolver {

    private final BenutzerRepository userRepository;

    public CurrentUserResolver(BenutzerRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Benutzer resolve(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> userRepository.save(new Benutzer(keycloakId)));
    }

}
