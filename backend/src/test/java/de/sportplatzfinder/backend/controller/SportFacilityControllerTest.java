package de.sportplatzfinder.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import de.sportplatzfinder.backend.model.Benutzer;
import de.sportplatzfinder.backend.model.Sportart;
import de.sportplatzfinder.backend.model.Sportplatz;
import de.sportplatzfinder.backend.model.Status;
import de.sportplatzfinder.backend.repository.BewertungRepository;
import de.sportplatzfinder.backend.repository.SportartRepository;
import de.sportplatzfinder.backend.repository.SportplatzRepository;

@WebMvcTest(SportFacilityController.class)
class SportFacilityControllerTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SportplatzRepository facilityRepository;
    @MockitoBean
    private SportartRepository sportTypeRepository;
    @MockitoBean
    private BewertungRepository ratingRepository;
    @MockitoBean
    private CurrentUserResolver currentUserResolver;

    private Sportplatz facility(long id, String name, Status status, String... sportTypes) {
        Sportplatz facility = new Sportplatz(new Benutzer("kc-1"), name, "Beschreibung",
                GEOMETRY_FACTORY.createPoint(new Coordinate(8.68, 50.11)), "Asphalt", true, 2, true);
        ReflectionTestUtils.setField(facility, "id", id);
        facility.setStatus(status);
        for (String type : sportTypes) {
            facility.getSportarten().add(new Sportart(type));
        }
        return facility;
    }

    @Test
    void returns401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/sportplaetze")).andExpect(status().isUnauthorized());
    }

    @Test
    void searchWithoutFiltersReturnsApprovedFacilities() throws Exception {
        when(facilityRepository.findByStatus(Status.APPROVED))
                .thenReturn(List.of(facility(1, "Bolzplatz", Status.APPROVED, "Fußball")));

        mockMvc.perform(get("/api/sportplaetze").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bolzplatz"))
                .andExpect(jsonPath("$[0].latitude").value(50.11))
                .andExpect(jsonPath("$[0].longitude").value(8.68))
                .andExpect(jsonPath("$[0].sportTypes[0]").value("Fußball"));
    }

    @Test
    void searchCombinesRadiusNameAndSportType() throws Exception {
        when(facilityRepository.findActiveInRadius(50.1, 8.6, 5000))
                .thenReturn(List.of(
                        facility(1, "Bolzplatz Nord", Status.APPROVED, "Fußball"),
                        facility(2, "Bolzplatz Süd", Status.APPROVED, "Basketball"),
                        facility(3, "Halle", Status.APPROVED, "Fußball")));

        mockMvc.perform(get("/api/sportplaetze").with(jwt())
                        .param("lat", "50.1").param("lon", "8.6").param("radius", "5000")
                        .param("name", "bolz").param("sportType", "fußball"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void incompleteRadiusSearchReturns400() throws Exception {
        mockMvc.perform(get("/api/sportplaetze").with(jwt()).param("lat", "50.1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detailsOfPendingFacilityReturns404() throws Exception {
        when(facilityRepository.findById(5L)).thenReturn(Optional.of(facility(5, "Neu", Status.PENDING)));

        mockMvc.perform(get("/api/sportplaetze/5").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void detailsContainRatingStatistics() throws Exception {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility(1, "Bolzplatz", Status.APPROVED)));
        when(ratingRepository.findDurchschnittSterne(1L)).thenReturn(4.5);
        when(ratingRepository.findBySportplatzId(1L)).thenReturn(List.of(mock(), mock()));

        mockMvc.perform(get("/api/sportplaetze/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facility.name").value("Bolzplatz"))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.ratingCount").value(2));
    }

    @Test
    void submitStoresFacilityAsPending() throws Exception {
        Sportart football = new Sportart("Fußball");
        ReflectionTestUtils.setField(football, "id", 1L);
        when(sportTypeRepository.findAllById(Set.of(1L))).thenReturn(List.of(football));
        when(currentUserResolver.resolve(any())).thenReturn(new Benutzer("kc-1"));
        when(facilityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String body = """
                {"name":"Neuer Platz","latitude":50.11,"longitude":8.68,"sportTypeIds":[1]}
                """;
        mockMvc.perform(post("/api/sportplaetze").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        ArgumentCaptor<Sportplatz> captor = ArgumentCaptor.forClass(Sportplatz.class);
        verify(facilityRepository).save(captor.capture());
        assertThat(captor.getValue().getStandort().getX()).isEqualTo(8.68);
        assertThat(captor.getValue().getStandort().getY()).isEqualTo(50.11);
    }

    @Test
    void submitWithInvalidDataReturns400() throws Exception {
        String body = """
                {"name":"","latitude":95,"longitude":8.68,"sportTypeIds":[]}
                """;
        mockMvc.perform(post("/api/sportplaetze").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verify(facilityRepository, never()).save(any());
    }

    @Test
    void submitWithUnknownSportTypeReturns400() throws Exception {
        when(sportTypeRepository.findAllById(Set.of(99L))).thenReturn(List.of());

        String body = """
                {"name":"Platz","latitude":50,"longitude":8,"sportTypeIds":[99]}
                """;
        mockMvc.perform(post("/api/sportplaetze").with(jwt()).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verify(facilityRepository, never()).save(any());
    }

}
