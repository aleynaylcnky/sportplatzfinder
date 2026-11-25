package de.sportplatzfinder.backend.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import de.sportplatzfinder.backend.model.Sportart;
import de.sportplatzfinder.backend.model.Sportplatz;
import de.sportplatzfinder.backend.model.Status;
import de.sportplatzfinder.backend.repository.BewertungRepository;
import de.sportplatzfinder.backend.repository.SportartRepository;
import de.sportplatzfinder.backend.repository.SportplatzRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@RequestMapping("/api/sportplaetze")
public class SportFacilityController {

    // SRID 4326 = WGS84 (Längen-/Breitengrad)
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final SportplatzRepository facilityRepository;
    private final SportartRepository sportTypeRepository;
    private final BewertungRepository ratingRepository;
    private final CurrentUserResolver currentUserResolver;

    public SportFacilityController(SportplatzRepository facilityRepository,
                                   SportartRepository sportTypeRepository,
                                   BewertungRepository ratingRepository,
                                   CurrentUserResolver currentUserResolver) {
        this.facilityRepository = facilityRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.ratingRepository = ratingRepository;
        this.currentUserResolver = currentUserResolver;
    }

    // ---------- Request / Response ----------

    public record SportFacilityRequest(
            @NotBlank @Size(max = 150) String name,
            String description,
            @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @Size(max = 100) String surface,
            Boolean lighting,
            @PositiveOrZero Integer hoopCount,
            Boolean freeOfCharge,
            @NotEmpty Set<Long> sportTypeIds) {
    }

    public record SportFacilityResponse(
            long id,
            String name,
            String description,
            double latitude,
            double longitude,
            String surface,
            Boolean lighting,
            Integer hoopCount,
            Boolean freeOfCharge,
            Status status,
            List<String> sportTypes) {
    }

    public record SportFacilityDetailResponse(
            SportFacilityResponse facility,
            Double averageRating,
            int ratingCount) {
    }

    // ---------- Endpunkte ----------

    /**
     * Suche nach freigegebenen Sportplätzen. Alle Filter sind optional und werden kombiniert.
     * Für die Umkreissuche müssen lat, lon und radius (in Metern) zusammen angegeben werden.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public List<SportFacilityResponse> search(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String sportType,
                                              @RequestParam(required = false) Double lat,
                                              @RequestParam(required = false) Double lon,
                                              @RequestParam(required = false) Double radius) {
        boolean radiusSearch = lat != null || lon != null || radius != null;
        if (radiusSearch && (lat == null || lon == null || radius == null || radius <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Für die Umkreissuche werden lat, lon und ein positiver radius benötigt");
        }

        List<Sportplatz> results;
        if (radiusSearch) {
            results = facilityRepository.findActiveInRadius(lat, lon, radius);
        } else if (name != null && !name.isBlank()) {
            results = facilityRepository.findByNameContainingIgnoreCaseAndStatus(name.trim(), Status.APPROVED);
        } else {
            results = facilityRepository.findByStatus(Status.APPROVED);
        }

        return results.stream()
                .filter(f -> name == null || name.isBlank()
                        || f.getName().toLowerCase().contains(name.trim().toLowerCase()))
                .filter(f -> sportType == null || sportType.isBlank()
                        || f.getSportarten().stream().anyMatch(t -> t.getBezeichnung().equalsIgnoreCase(sportType.trim())))
                .map(SportFacilityController::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public SportFacilityDetailResponse getDetails(@PathVariable long id) {
        Sportplatz facility = facilityRepository.findById(id)
                .filter(f -> f.getStatus() == Status.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sportplatz nicht gefunden"));

        Double averageRating = ratingRepository.findDurchschnittSterne(id);
        int ratingCount = ratingRepository.findBySportplatzId(id).size();
        return new SportFacilityDetailResponse(toResponse(facility), averageRating, ratingCount);
    }

    /** Neuer Sportplatz wird als PENDING gespeichert und erscheint erst nach Freigabe durch einen Admin. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public SportFacilityResponse submit(@Valid @RequestBody SportFacilityRequest request,
                                        @AuthenticationPrincipal Jwt jwt) {
        List<Sportart> sportTypes = sportTypeRepository.findAllById(request.sportTypeIds());
        if (sportTypes.size() != request.sportTypeIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unbekannte Sportart angegeben");
        }

        Point location = GEOMETRY_FACTORY.createPoint(new Coordinate(request.longitude(), request.latitude()));
        Sportplatz facility = new Sportplatz(currentUserResolver.resolve(jwt), request.name().trim(),
                request.description(), location, request.surface(), request.lighting(),
                request.hoopCount(), request.freeOfCharge());
        facility.setStatus(Status.PENDING);
        facility.setSportarten(new HashSet<>(sportTypes));

        return toResponse(facilityRepository.save(facility));
    }

    // ---------- Hilfsmethoden ----------

    static SportFacilityResponse toResponse(Sportplatz facility) {
        List<String> sportTypes = facility.getSportarten().stream()
                .map(Sportart::getBezeichnung)
                .sorted()
                .toList();
        // JTS: x = Längengrad, y = Breitengrad
        return new SportFacilityResponse(facility.getId(), facility.getName(), facility.getBeschreibung(),
                facility.getStandort().getY(), facility.getStandort().getX(), facility.getUntergrund(),
                facility.getBeleuchtung(), facility.getAnzahlKoerbe(), facility.getKostenlos(),
                facility.getStatus(), sportTypes);
    }

}
