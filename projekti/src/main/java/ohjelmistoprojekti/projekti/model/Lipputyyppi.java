package ohjelmistoprojekti.projekti.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Lipputyyppi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long lipputyyppiId;

    @JsonIgnoreProperties("lipputyypit")
    @ManyToOne
    @JoinColumn(name = "tapahtuma_id")
    private Tapahtuma tapahtuma;

    private String nimi;
    private String kuvaus;
    private double hinta;

    @JsonIgnoreProperties("lipputyyppi")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lipputyyppi")
    private List<Lippu> liput;

    @JsonIgnoreProperties("lipputyyppi")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lipputyyppi")
    private List<Myyntirivi> myyntirivit;

    public Lipputyyppi() {
    }

    public Lipputyyppi(Long lipputyyppiId, Tapahtuma tapahtuma, String nimi, String kuvaus, double hinta,
            List<Lippu> liput, List<Myyntirivi> myyntirivit) {
        this.lipputyyppiId = lipputyyppiId;
        this.tapahtuma = tapahtuma;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.liput = liput;
        this.myyntirivit = myyntirivit;
    }

    public Long getLipputyyppiId() {
        return lipputyyppiId;
    }

    public void setLipputyyppiId(Long lipputyyppiId) {
        this.lipputyyppiId = lipputyyppiId;
    }

    public Tapahtuma getTapahtuma() {
        return tapahtuma;
    }

    public void setTapahtuma(Tapahtuma tapahtuma) {
        this.tapahtuma = tapahtuma;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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

    public List<Lippu> getLiput() {
        return liput;
    }

    public void setLiput(List<Lippu> liput) {
        this.liput = liput;
    }

    public List<Myyntirivi> getMyyntirivit() {
        return myyntirivit;
    }

    public void setMyyntirivit(List<Myyntirivi> myyntirivit) {
        this.myyntirivit = myyntirivit;
    }

    @Override
    public String toString() {
        return "Lipputyyppi [lipputyyppiId=" + lipputyyppiId + ", nimi=" + nimi + ", kuvaus=" + kuvaus
                + ", hinta=" + hinta + "]";
    }
}
