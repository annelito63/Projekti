package ohjelmistoprojekti.projekti.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Tarkastus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tarkastusId;

    @JsonIgnoreProperties("tarkastukset")
    @ManyToOne
    @JoinColumn(name = "lippu_id")
    private Lippu lippu;

    private LocalDateTime tarkastusaika;

    @JsonIgnoreProperties("tarkastukset")
    @ManyToOne
    @JoinColumn(name = "tarkastaja_id")
    private Kayttaja tarkastaja;

    public Tarkastus() {
    }

    public Tarkastus(Long tarkastusId, Lippu lippu, LocalDateTime tarkastusaika, Kayttaja tarkastaja) {
        this.tarkastusId = tarkastusId;
        this.lippu = lippu;
        this.tarkastusaika = tarkastusaika;
        this.tarkastaja = tarkastaja;
    }

    public Long getTarkastusId() {
        return tarkastusId;
    }

    public void setTarkastusId(Long tarkastusId) {
        this.tarkastusId = tarkastusId;
    }

    public Lippu getLippu() {
        return lippu;
    }

    public void setLippu(Lippu lippu) {
        this.lippu = lippu;
    }

    public LocalDateTime getTarkastusaika() {
        return tarkastusaika;
    }

    public void setTarkastusaika(LocalDateTime tarkastusaika) {
        this.tarkastusaika = tarkastusaika;
    }

    public Kayttaja getTarkastaja() {
        return tarkastaja;
    }

    public void setTarkastaja(Kayttaja tarkastaja) {
        this.tarkastaja = tarkastaja;
    }

    @Override
    public String toString() {
        return "Tarkastus [tarkastusId=" + tarkastusId + ", tarkastusaika=" + tarkastusaika + "]";
    }
}
