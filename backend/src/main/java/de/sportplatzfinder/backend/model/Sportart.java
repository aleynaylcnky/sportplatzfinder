package de.sportplatzfinder.backend.model;
import jakarta.persistence.*;

@Entity
@Table(name = "sportart")
public class Sportart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sportart_id")
    private long id;

    @Column(name = "bezeichnung", nullable = false, unique = true, length = 100)
    private String bezeichnung;

    public Sportart() {

    }

    public Sportart(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public long getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String toString() {
        return "Sportart [id=" + id + ", bezeichnung=" + bezeichnung + "]";
    }

}