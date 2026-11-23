package de.sportplatzfinder.backend.model;


import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "sportplatz")
public class Sportplatz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sportplatz_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "benutzer_id", nullable = false)
    private Benutzer benutzer;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "beschreibung", columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "standort", nullable = false, columnDefinition = "geography(Point,4326)")
    private Point standort;

    @Column(name = "untergrund", length = 100)
    private String untergrund;

    @Column(name = "beleuchtung")
    private Boolean beleuchtung;

    @Column(name = "anzahl_koerbe")
    private Integer anzahlKoerbe;

    @Column(name = "kostenlos")
    private Boolean kostenlos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    // n:m zu Sportart, Zwischentabelle sportplatz_sportart (zusammengesetzter PK)
    @ManyToMany
    @JoinTable(
        name = "sportplatz_sportart",
        joinColumns = @JoinColumn(name = "sportplatz_id"),
        inverseJoinColumns = @JoinColumn(name = "sportart_id")
    )
    private Set<Sportart> sportarten = new HashSet<>();

    public Sportplatz() {

    }

    public Sportplatz(Benutzer benutzer, String name, String beschreibung, Point standort,
                      String untergrund, Boolean beleuchtung, Integer anzahlKoerbe, Boolean kostenlos) {
        this.benutzer = benutzer;
        this.name = name;
        this.beschreibung = beschreibung;
        this.standort = standort;
        this.untergrund = untergrund;
        this.beleuchtung = beleuchtung;
        this.anzahlKoerbe = anzahlKoerbe;
        this.kostenlos = kostenlos;
    }

    public long getId() {
        return id;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Point getStandort() {
        return standort;
    }

    public void setStandort(Point standort) {
        this.standort = standort;
    }

    public String getUntergrund() {
        return untergrund;
    }

    public void setUntergrund(String untergrund) {
        this.untergrund = untergrund;
    }

    public Boolean getBeleuchtung() {
        return beleuchtung;
    }

    public void setBeleuchtung(Boolean beleuchtung) {
        this.beleuchtung = beleuchtung;
    }

    public Integer getAnzahlKoerbe() {
        return anzahlKoerbe;
    }

    public void setAnzahlKoerbe(Integer anzahlKoerbe) {
        this.anzahlKoerbe = anzahlKoerbe;
    }

    public Boolean getKostenlos() {
        return kostenlos;
    }

    public void setKostenlos(Boolean kostenlos) {
        this.kostenlos = kostenlos;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Sportart> getSportarten() {
        return sportarten;
    }

    public void setSportarten(Set<Sportart> sportarten) {
        this.sportarten = sportarten;
    }

    @Override
    public String toString() {
        return "Sportplatz [id=" + id + ", name=" + name + ", untergrund=" + untergrund
                + ", beleuchtung=" + beleuchtung + ", anzahlKoerbe=" + anzahlKoerbe
                + ", kostenlos=" + kostenlos + ", status=" + status + "]";
    }

}
