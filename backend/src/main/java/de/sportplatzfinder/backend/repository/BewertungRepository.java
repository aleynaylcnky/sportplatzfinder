package de.sportplatzfinder.backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.sportplatzfinder.backend.model.Bewertung;

public interface BewertungRepository extends JpaRepository<Bewertung, Long> {

  List<Bewertung> findBySportplatzId(long sportplatzId);

  List<Bewertung> findByBenutzerId(long benutzerId);

  @Query("SELECT AVG(b.sterne) FROM Bewertung b WHERE b.sportplatz.id = :sportplatzId")
  Double findDurchschnittSterne(@Param("sportplatzId") long sportplatzId);

}