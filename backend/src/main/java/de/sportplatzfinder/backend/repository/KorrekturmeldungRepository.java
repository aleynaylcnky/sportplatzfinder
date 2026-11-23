package de.sportplatzfinder.backend.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.sportplatzfinder.backend.model.Korrekturmeldung;
import de.sportplatzfinder.backend.model.Status;

public interface KorrekturmeldungRepository extends JpaRepository<Korrekturmeldung, Long> {

  List<Korrekturmeldung> findByStatus(Status status);

  List<Korrekturmeldung> findBySportplatzId(long sportplatzId);

}
