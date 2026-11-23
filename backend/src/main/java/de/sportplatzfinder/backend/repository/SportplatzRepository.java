package de.sportplatzfinder.backend.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.sportplatzfinder.backend.model.Sportplatz;
import de.sportplatzfinder.backend.model.Status;

public interface SportplatzRepository extends JpaRepository<Sportplatz, Long> {

  List<Sportplatz> findByStatus(Status status);

  List<Sportplatz> findByNameContainingIgnoreCaseAndStatus(String name, Status status);

  List<Sportplatz> findByBenutzerId(long benutzerId);

  List<Sportplatz> findBySportartenBezeichnungAndStatus(String bezeichnung, Status status);

  // Umkreissuche mit PostGIS, radiusMeter in Metern (geography)
  @Query(value = """
      SELECT * FROM sportplatz
      WHERE status = 'APPROVED'
        AND ST_DWithin(standort, CAST(ST_SetSRID(ST_MakePoint(:lon, :lat), 4326) AS geography), :radiusMeter)
      """, nativeQuery = true)
  List<Sportplatz> findActiveInRadius(@Param("lat") double lat,
                                      @Param("lon") double lon,
                                      @Param("radiusMeter") double radiusMeter);

}
