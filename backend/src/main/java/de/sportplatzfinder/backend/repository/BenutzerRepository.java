package de.sportplatzfinder.backend.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.sportplatzfinder.backend.model.Benutzer;

public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {

  Optional<Benutzer> findByKeycloakId(String keycloakId);

}
