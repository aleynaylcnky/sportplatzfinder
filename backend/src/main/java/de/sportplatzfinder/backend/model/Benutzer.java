package de.sportplatzfinder.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "benutzer")
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benutzer_id")
    private long id;

    @Column(name = "keycloak_id", nullable = false, unique = true, length = 255)
    private String keycloakId;

    public Benutzer() {

    }

    public Benutzer(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public long getId() {
        return id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    @Override
    public String toString() {
        return "Benutzer [id=" + id + ", keycloakId=" + keycloakId + "]";
    }

}