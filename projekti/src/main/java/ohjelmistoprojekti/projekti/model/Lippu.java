package ohjelmistoprojekti.projekti.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Lippu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tapahtumaid")
    private Tapahtuma tapahtumaid;

    private String koodi;
    private String kuvaus;
    private double hinta;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lipputyyppi_id")
    private Lipputyyppi lipputyyppi;

    private boolean kaytetty = false;

    public Lippu() {
    }

    public Lippu(Long id, Tapahtuma tapahtumaid, String koodi, String kuvaus, double hinta, Lipputyyppi lipputyyppi) {
        this.id = id;
        this.tapahtumaid = tapahtumaid;
        this.koodi = koodi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.lipputyyppi = lipputyyppi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tapahtuma getTapahtumaid() {
        return tapahtumaid;
    }

    public void setTapahtumaid(Tapahtuma tapahtumaid) {
        this.tapahtumaid = tapahtumaid;
    }

    public boolean isKaytetty() {
        return kaytetty;
    }

    public void setKaytetty(boolean kaytetty) {
        this.kaytetty = kaytetty;
    }

    public String getKoodi() {
        return koodi;
    }

    public void setKoodi(String koodi) {
        this.koodi = koodi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public double getHinta() {
        return hinta;
    }

    public void setHinta(double hinta) {
        this.hinta = hinta;
    }

    public Lipputyyppi getLipputyyppi() {
        return lipputyyppi;
    }

    public void setLipputyyppi(Lipputyyppi lipputyyppi) {
        this.lipputyyppi = lipputyyppi;
    }

    @Override
    public String toString() {
        return "Lippu [id=" + id + ", tapahtumaid=" + tapahtumaid + ", koodi=" + koodi + ", kuvaus=" + kuvaus
                + ", hinta=" + hinta + ", lipputyyppi=" + lipputyyppi + "]";
    }
}