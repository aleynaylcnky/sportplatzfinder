package de.sportplatzfinder.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import de.sportplatzfinder.backend.model.Benutzer;
import de.sportplatzfinder.backend.model.Bewertung;
import de.sportplatzfinder.backend.model.Sportplatz;
import de.sportplatzfinder.backend.model.Status;
import de.sportplatzfinder.backend.repository.BewertungRepository;
import de.sportplatzfinder.backend.repository.SportplatzRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/sportplaetze/{facilityId}/bewertungen")
public class RatingController {

    private final BewertungRepository ratingRepository;
    private final SportplatzRepository facilityRepository;
    private final CurrentUserResolver currentUserResolver;

    public RatingController(BewertungRepository ratingRepository,
                            SportplatzRepository facilityRepository,
                            CurrentUserResolver currentUserResolver) {
        this.ratingRepository = ratingRepository;
        this.facilityRepository = facilityRepository;
        this.currentUserResolver = currentUserResolver;
    }

    // ---------- Request / Response ----------

    public record RatingRequest(
            @NotNull @Min(1) @Max(5) Integer stars,
            @Size(max = 2000) String text) {
    }

    /** "ownRating" zeigt dem Frontend, ob die Bewertung vom eingeloggten Benutzer stammt (Bearbeiten-Button). */
    public record RatingResponse(
            long id,
            int stars,
            String text,
            LocalDateTime createdAt,
            boolean ownRating) {
    }

    // ---------- Endpunkte ----------

    @GetMapping
    @Transactional
    public List<RatingResponse> list(@PathVariable long facilityId,
                                     @AuthenticationPrincipal Jwt jwt) {
        loadApprovedFacility(facilityId);
        Benutzer currentUser = currentUserResolver.resolve(jwt);
        return ratingRepository.findBySportplatzId(facilityId).stream()
                .map(r -> toResponse(r, currentUser))
                .toList();
    }

    /** Pro Benutzer und Sportplatz ist nur eine Bewertung erlaubt. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public RatingResponse create(@PathVariable long facilityId,
                                 @Valid @RequestBody RatingRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        Sportplatz facility = loadApprovedFacility(facilityId);
        Benutzer currentUser = currentUserResolver.resolve(jwt);

        boolean alreadyRated = ratingRepository.findByBenutzerId(currentUser.getId()).stream()
                .anyMatch(r -> r.getSportplatz().getId() == facilityId);
        if (alreadyRated) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Du hast diesen Sportplatz bereits bewertet");
        }

        Bewertung rating = new Bewertung(currentUser, facility, request.stars(), request.text());
        return toResponse(ratingRepository.save(rating), currentUser);
    }

    /** Nur die eigene Bewertung darf bearbeitet werden. */
    @PutMapping("/{ratingId}")
    @Transactional
    public RatingResponse update(@PathVariable long facilityId,
                                 @PathVariable long ratingId,
                                 @Valid @RequestBody RatingRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        Bewertung rating = ratingRepository.findById(ratingId)
                .filter(r -> r.getSportplatz().getId() == facilityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bewertung nicht gefunden"));

        Benutzer currentUser = currentUserResolver.resolve(jwt);
        if (rating.getBenutzer().getId() != currentUser.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Nur eigene Bewertungen dürfen bearbeitet werden");
        }

        rating.setSterne(request.stars());
        rating.setText(request.text());
        return toResponse(ratingRepository.save(rating), currentUser);
    }

    // ---------- Hilfsmethoden ----------

    private Sportplatz loadApprovedFacility(long facilityId) {
        return facilityRepository.findById(facilityId)
                .filter(f -> f.getStatus() == Status.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sportplatz nicht gefunden"));
    }

    private static RatingResponse toResponse(Bewertung rating, Benutzer currentUser) {
        return new RatingResponse(rating.getId(), rating.getSterne(), rating.getText(), rating.getErstelltAm(),
                rating.getBenutzer().getId() == currentUser.getId());
    }

}
