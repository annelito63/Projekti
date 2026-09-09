import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Tapahtuma {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tapahtumaid;
    private String nimi;
    private String tyyppi;
    private String aika;
    private String kaupunki;
    private String paikka;
    private String kuvaus;
    private int maara;

    @JsonIgnoreProperties("tapahtumaid")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tapahtumaid")
    private List<Lippu> liput;

    public Tapahtuma(List<Lippu> liput) {
        this.liput = liput;
    }

    public Tapahtuma(Long tapahtumaid, String nimi, String tyyppi, String aika, String kaupunki, String paikka,
            String kuvaus, int maara, List<Lippu> liput) {
        this.tapahtumaid = tapahtumaid;
        this.nimi = nimi;
        this.tyyppi = tyyppi;
        this.aika = aika;
        this.kaupunki = kaupunki;
        this.paikka = paikka;
        this.kuvaus = kuvaus;
        this.maara = maara;
        this.liput = liput;
    }

    public Long getTapahtumaid() {
        return tapahtumaid;
    }

    public void setTapahtumaid(Long tapahtumaid) {
        this.tapahtumaid = tapahtumaid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    public String getAika() {
        return aika;
    }

    public void setAika(String aika) {
        this.aika = aika;
    }

    public String getKaupunki() {
        return kaupunki;
    }

    public void setKaupunki(String kaupunki) {
        this.kaupunki = kaupunki;
    }

    public String getPaikka() {
        return paikka;
    }

    public void setPaikka(String paikka) {
        this.paikka = paikka;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public int getMaara() {
        return maara;
    }

    public void setMaara(int maara) {
        this.maara = maara;
    }

    public List<Lippu> getLiput() {
        return liput;
    }

    public void setLiput(List<Lippu> liput) {
        this.liput = liput;
    }

    @Override
    public String toString() {
        return "Tapahtuma [tapahtumaid=" + tapahtumaid + ", nimi=" + nimi + ", tyyppi=" + tyyppi + ", aika=" + aika
                + ", kaupunki=" + kaupunki + ", paikka=" + paikka + ", kuvaus=" + kuvaus + ", maara=" + maara
                + ", liput=" + liput + "]";
    }

    
}
