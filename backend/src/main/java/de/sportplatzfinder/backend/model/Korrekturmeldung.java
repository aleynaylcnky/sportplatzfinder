package de.sportplatzfinder.backend.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "korrekturmeldung")
public class Korrekturmeldung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meldung_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "benutzer_id", nullable = false)
    private Benutzer benutzer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sportplatz_id", nullable = false)
    private Sportplatz sportplatz;

    @Column(name = "beschreibung", nullable = false, columnDefinition = "TEXT")
    private String beschreibung;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm;

    public Korrekturmeldung() {

    }

    public Korrekturmeldung(Benutzer benutzer, Sportplatz sportplatz, String beschreibung) {
        this.benutzer = benutzer;
        this.sportplatz = sportplatz;
        this.beschreibung = beschreibung;
    }

    @PrePersist
    protected void onCreate() {
        this.erstelltAm = LocalDateTime.now();
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

    public Sportplatz getSportplatz() {
        return sportplatz;
    }

    public void setSportplatz(Sportplatz sportplatz) {
        this.sportplatz = sportplatz;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    @Override
    public String toString() {
        return "Korrekturmeldung [id=" + id + ", beschreibung=" + beschreibung
                + ", status=" + status + ", erstelltAm=" + erstelltAm + "]";
    }

}