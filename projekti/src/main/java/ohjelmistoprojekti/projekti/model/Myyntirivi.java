package ohjelmistoprojekti.projekti.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Myyntirivi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myyntiriviId;

    @JsonIgnoreProperties("myyntirivit")
    @ManyToOne
    @JoinColumn(name = "myynti_id")
    private Myyntitapahtuma myynti;

    @JsonIgnoreProperties("myyntirivit")
    @ManyToOne
    @JoinColumn(name = "lipputyyppi_id")
    private Lipputyyppi lipputyyppi;

    private int maara;
    private double yksikkohinta;
    private double rivisumma;

    public Myyntirivi() {
    }

    public Myyntirivi(Long myyntiriviId, Myyntitapahtuma myynti, Lipputyyppi lipputyyppi, int maara,
            double yksikkohinta, double rivisumma) {
        this.myyntiriviId = myyntiriviId;
        this.myynti = myynti;
        this.lipputyyppi = lipputyyppi;
        this.maara = maara;
        this.yksikkohinta = yksikkohinta;
        this.rivisumma = rivisumma;
    }

    public Long getMyyntiriviId() {
        return myyntiriviId;
    }

    public void setMyyntiriviId(Long myyntiriviId) {
        this.myyntiriviId = myyntiriviId;
    }

    public Myyntitapahtuma getMyynti() {
        return myynti;
    }

    public void setMyynti(Myyntitapahtuma myynti) {
        this.myynti = myynti;
    }

    public Lipputyyppi getLipputyyppi() {
        return lipputyyppi;
    }

    public void setLipputyyppi(Lipputyyppi lipputyyppi) {
        this.lipputyyppi = lipputyyppi;
    }

    public int getMaara() {
        return maara;
    }

    public void setMaara(int maara) {
        this.maara = maara;
    }

    public double getYksikkohinta() {
        return yksikkohinta;
    }

    public void setYksikkohinta(double yksikkohinta) {
        this.yksikkohinta = yksikkohinta;
    }

    public double getRivisumma() {
        return rivisumma;
    }

    public void setRivisumma(double rivisumma) {
        this.rivisumma = rivisumma;
    }

    @Override
    public String toString() {
        return "Myyntirivi [myyntiriviId=" + myyntiriviId + ", maara=" + maara
                + ", yksikkohinta=" + yksikkohinta + ", rivisumma=" + rivisumma + "]";
    }
}
