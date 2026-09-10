import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Kayttaja {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String nimi;
    private String sahkoposti;

    @Enumerated(EnumType.STRING)
    private Rooli rooli;

    @JsonIgnoreProperties("kayttaja")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kayttaja")
    private List<Myyntitapahtuma> myynnit;

    @JsonIgnoreProperties("tarkastaja")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tarkastaja")
    private List<Tarkastus> tarkastukset;

    public Kayttaja() {
    }

    public Kayttaja(Long userId, String nimi, String sahkoposti, Rooli rooli,
            List<Myyntitapahtuma> myynnit, List<Tarkastus> tarkastukset) {
        this.userId = userId;
        this.nimi = nimi;
        this.sahkoposti = sahkoposti;
        this.rooli = rooli;
        this.myynnit = myynnit;
        this.tarkastukset = tarkastukset;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getSahkoposti() {
        return sahkoposti;
    }

    public void setSahkoposti(String sahkoposti) {
        this.sahkoposti = sahkoposti;
    }

    public Rooli getRooli() {
        return rooli;
    }

    public void setRooli(Rooli rooli) {
        this.rooli = rooli;
    }

    public List<Myyntitapahtuma> getMyynnit() {
        return myynnit;
    }

    public void setMyynnit(List<Myyntitapahtuma> myynnit) {
        this.myynnit = myynnit;
    }

    public List<Tarkastus> getTarkastukset() {
        return tarkastukset;
    }

    public void setTarkastukset(List<Tarkastus> tarkastukset) {
        this.tarkastukset = tarkastukset;
    }

    @Override
    public String toString() {
        return "Kayttaja [userId=" + userId + ", nimi=" + nimi + ", sahkoposti=" + sahkoposti
                + ", rooli=" + rooli + "]";
    }
}
