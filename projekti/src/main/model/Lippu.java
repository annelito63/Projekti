import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Lippu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnoreProperties("liput")
    @ManyToOne
    @JoinColumn(name = "tapahtumaid")
    private Tapahtuma tapahtumaid;
    private String nimi;
    private String kuvaus;
    private double hinta;

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

    public Lippu() {
    }

    public Lippu(Long id, Tapahtuma tapahtumaid, String nimi, String kuvaus, double hinta) {
        this.id = id;
        this.tapahtumaid = tapahtumaid;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
    }

    @Override
    public String toString() {
        return "Lippu [id=" + id + ", tapahtumaid=" + tapahtumaid + ", nimi=" + nimi + ", kuvaus=" + kuvaus + ", hinta="
                + hinta + "]";
    }

}
