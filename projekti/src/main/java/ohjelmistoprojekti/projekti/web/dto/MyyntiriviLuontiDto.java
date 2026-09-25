package ohjelmistoprojekti.projekti.web.dto;

/**
 * Yhden myyntirivin tiedot myyntitapahtuman luontipyynnössä.
 * Asiakas antaa vain lipputyypin id:n ja määrän - yksikköhinta ja
 * rivisumma lasketaan palvelimella lipputyypin senhetkisestä hinnasta.
 */
public class MyyntiriviLuontiDto {

    private Long lipputyyppiId;
    private int maara;

    public MyyntiriviLuontiDto() {
    }

    public MyyntiriviLuontiDto(Long lipputyyppiId, int maara) {
        this.lipputyyppiId = lipputyyppiId;
        this.maara = maara;
    }

    public Long getLipputyyppiId() {
        return lipputyyppiId;
    }

    public void setLipputyyppiId(Long lipputyyppiId) {
        this.lipputyyppiId = lipputyyppiId;
    }

    public int getMaara() {
        return maara;
    }

    public void setMaara(int maara) {
        this.maara = maara;
    }
}
