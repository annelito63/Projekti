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

    @JsonIgnoreProperties("tyyppiid")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tyyppiid")
    private List<Tyyppi> tyypit;

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


    public List<Tyyppi> getTyypit() {
        return tyypit;
    }

    public void setTyypit(List<Tyyppi> tyypit) {
        this.tyypit = tyypit;
    }

    public Lippu(Long id, Tapahtuma tapahtumaid, String nimi, String kuvaus, double hinta, List<Tyyppi> tyypit) {
        this.id = id;
        this.tapahtumaid = tapahtumaid;
        this.nimi = nimi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.tyypit = tyypit;
    }

    @Override
    public String toString() {
        return "Lippu [id=" + id + ", tapahtumaid=" + tapahtumaid + ", nimi=" + nimi + ", kuvaus=" + kuvaus + ", hinta="
                + hinta + ", tyypit=" + tyypit + "]";
    }

    

}
