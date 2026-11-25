package de.sportplatzfinder.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import de.sportplatzfinder.backend.model.Benutzer;
import de.sportplatzfinder.backend.model.Bewertung;
import de.sportplatzfinder.backend.model.Sportplatz;
import de.sportplatzfinder.backend.model.Status;
import de.sportplatzfinder.backend.repository.BewertungRepository;
import de.sportplatzfinder.backend.repository.SportplatzRepository;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BewertungRepository ratingRepository;
    @MockitoBean
    private SportplatzRepository facilityRepository;
    @MockitoBean
    private CurrentUserResolver currentUserResolver;

    private Benutzer currentUser;
    private Benutzer otherUser;
    private Sportplatz facility;

    private static Benutzer user(long id, String keycloakId) {
        Benutzer user = new Benutzer(keycloakId);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Bewertung rating(long id, Benutzer author, int stars) {
        Bewertung rating = new Bewertung(author, facility, stars, "Text");
        ReflectionTestUtils.setField(rating, "id", id);
        return rating;
    }

    @BeforeEach
    void setUp() {
        currentUser = user(1, "kc-current");
        otherUser = user(2, "kc-other");
        facility = new Sportplatz(otherUser, "Bolzplatz", null,
                new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(8.68, 50.11)),
                null, null, null, null);
        ReflectionTestUtils.setField(facility, "id", 10L);
        facility.setStatus(Status.APPROVED);

        when(facilityRepository.findById(10L)).thenReturn(Optional.of(facility));
        when(currentUserResolver.resolve(any())).thenReturn(currentUser);
        when(ratingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void listMarksOwnRating() throws Exception {
        when(ratingRepository.findBySportplatzId(10L))
                .thenReturn(List.of(rating(1, currentUser, 5), rating(2, otherUser, 3)));

        mockMvc.perform(get("/api/sportplaetze/10/bewertungen").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownRating").value(true))
                .andExpect(jsonPath("$[1].ownRating").value(false));
    }

    @Test
    void createReturns201() throws Exception {
        when(ratingRepository.findByBenutzerId(1L)).thenReturn(List.of());

        mockMvc.perform(post("/api/sportplaetze/10/bewertungen").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":4,\"text\":\"Gut\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stars").value(4))
                .andExpect(jsonPath("$.ownRating").value(true));
    }

    @Test
    void secondRatingBySameUserReturns409() throws Exception {
        when(ratingRepository.findByBenutzerId(1L)).thenReturn(List.of(rating(1, currentUser, 5)));

        mockMvc.perform(post("/api/sportplaetze/10/bewertungen").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":3}"))
                .andExpect(status().isConflict());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void starsOutsideOneToFiveReturns400() throws Exception {
        mockMvc.perform(post("/api/sportplaetze/10/bewertungen").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":6}"))
                .andExpect(status().isBadRequest());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void ratingPendingFacilityReturns404() throws Exception {
        facility.setStatus(Status.PENDING);

        mockMvc.perform(post("/api/sportplaetze/10/bewertungen").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":3}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOwnRating() throws Exception {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(rating(1, currentUser, 2)));

        mockMvc.perform(put("/api/sportplaetze/10/bewertungen/1").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":5,\"text\":\"Doch super\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stars").value(5))
                .andExpect(jsonPath("$.text").value("Doch super"));
    }

    @Test
    void updatingOtherUsersRatingReturns403() throws Exception {
        when(ratingRepository.findById(2L)).thenReturn(Optional.of(rating(2, otherUser, 1)));

        mockMvc.perform(put("/api/sportplaetze/10/bewertungen/2").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"stars\":5}"))
                .andExpect(status().isForbidden());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void returns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/sportplaetze/10/bewertungen")).andExpect(status().isUnauthorized());
    }

}
