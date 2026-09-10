import java.time.LocalDateTime;
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
public class Myyntitapahtuma {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myyntiId;

    @JsonIgnoreProperties("myynnit")
    @ManyToOne
    @JoinColumn(name = "tapahtuma_id")
    private Tapahtuma tapahtuma;

    @JsonIgnoreProperties("myynnit")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Kayttaja kayttaja;

    private LocalDateTime myyntiaika;
    private double kokonaissumma;

    @JsonIgnoreProperties("myynti")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "myynti")
    private List<Myyntirivi> myyntirivit;

    public Myyntitapahtuma() {
    }

    public Myyntitapahtuma(Long myyntiId, Tapahtuma tapahtuma, Kayttaja kayttaja, LocalDateTime myyntiaika,
            double kokonaissumma, List<Myyntirivi> myyntirivit) {
        this.myyntiId = myyntiId;
        this.tapahtuma = tapahtuma;
        this.kayttaja = kayttaja;
        this.myyntiaika = myyntiaika;
        this.kokonaissumma = kokonaissumma;
        this.myyntirivit = myyntirivit;
    }

    public Long getMyyntiId() {
        return myyntiId;
    }

    public void setMyyntiId(Long myyntiId) {
        this.myyntiId = myyntiId;
    }

    public Tapahtuma getTapahtuma() {
        return tapahtuma;
    }

    public void setTapahtuma(Tapahtuma tapahtuma) {
        this.tapahtuma = tapahtuma;
    }

    public Kayttaja getKayttaja() {
        return kayttaja;
    }

    public void setKayttaja(Kayttaja kayttaja) {
        this.kayttaja = kayttaja;
    }

    public LocalDateTime getMyyntiaika() {
        return myyntiaika;
    }

    public void setMyyntiaika(LocalDateTime myyntiaika) {
        this.myyntiaika = myyntiaika;
    }

    public double getKokonaissumma() {
        return kokonaissumma;
    }

    public void setKokonaissumma(double kokonaissumma) {
        this.kokonaissumma = kokonaissumma;
    }

    public List<Myyntirivi> getMyyntirivit() {
        return myyntirivit;
    }

    public void setMyyntirivit(List<Myyntirivi> myyntirivit) {
        this.myyntirivit = myyntirivit;
    }

    @Override
    public String toString() {
        return "Myyntitapahtuma [myyntiId=" + myyntiId + ", myyntiaika=" + myyntiaika
                + ", kokonaissumma=" + kokonaissumma + "]";
    }
}
