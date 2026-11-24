package de.sportplatzfinder.backend.model;


import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foto_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sportplatz_id", nullable = false)
    private Sportplatz sportplatz;

    // bewusst ohne @Lob, sonst mappt Hibernate bei PostgreSQL auf "oid" statt "bytea"
    @Column(name = "bild", nullable = false, columnDefinition = "bytea")
    private byte[] bild;

    public Foto() {

    }

    public Foto(Sportplatz sportplatz, byte[] bild) {
        this.sportplatz = sportplatz;
        this.bild = bild;
    }

    public long getId() {
        return id;
    }

    public Sportplatz getSportplatz() {
        return sportplatz;
    }

    public void setSportplatz(Sportplatz sportplatz) {
        this.sportplatz = sportplatz;
    }

    public byte[] getBild() {
        return bild;
    }

    public void setBild(byte[] bild) {
        this.bild = bild;
    }

    @Override
    public String toString() {
        return "Foto [id=" + id + ", bildGroesse=" + (bild != null ? bild.length : 0) + " Bytes]";
    }

}