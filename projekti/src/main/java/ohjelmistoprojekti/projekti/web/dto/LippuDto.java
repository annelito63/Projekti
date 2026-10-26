package ohjelmistoprojekti.projekti.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.Tapahtuma;

public class LippuDto {
    private Long id;

    @JsonIgnoreProperties("liput")
    @ManyToOne
    @JoinColumn(name = "tapahtumaid")
    private Tapahtuma tapahtumaid;

    private String koodi;
    private String kuvaus;
    private double hinta;

    @JsonIgnoreProperties("liput")
    @ManyToOne
    @JoinColumn(name = "lipputyyppi_id")
    private Lipputyyppi lipputyyppi;

    private boolean kaytetty = false;

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

    public boolean isKaytetty() {
        return kaytetty;
    }

    public void setKaytetty(boolean kaytetty) {
        this.kaytetty = kaytetty;
    }

    public LippuDto() {
    }

    public LippuDto(Long id, Tapahtuma tapahtumaid, String koodi, String kuvaus, double hinta, Lipputyyppi lipputyyppi,
            boolean kaytetty) {
        this.id = id;
        this.tapahtumaid = tapahtumaid;
        this.koodi = koodi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.lipputyyppi = lipputyyppi;
        this.kaytetty = kaytetty;
    }

    

}
