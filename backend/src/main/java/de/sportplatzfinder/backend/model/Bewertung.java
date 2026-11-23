package de.sportplatzfinder.backend.model;



import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bewertung")
public class Bewertung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bewertung_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "benutzer_id", nullable = false)
    private Benutzer benutzer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sportplatz_id", nullable = false)
    private Sportplatz sportplatz;

    @Column(name = "sterne", nullable = false)
    private Integer sterne;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "erstellt_am", nullable = false, updatable = false)
    private LocalDateTime erstelltAm;

    public Bewertung() {

    }

    public Bewertung(Benutzer benutzer, Sportplatz sportplatz, Integer sterne, String text) {
        this.benutzer = benutzer;
        this.sportplatz = sportplatz;
        this.sterne = sterne;
        this.text = text;
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

    public Integer getSterne() {
        return sterne;
    }

    public void setSterne(Integer sterne) {
        this.sterne = sterne;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    @Override
    public String toString() {
        return "Bewertung [id=" + id + ", sterne=" + sterne + ", text=" + text + ", erstelltAm=" + erstelltAm + "]";
    }

}
