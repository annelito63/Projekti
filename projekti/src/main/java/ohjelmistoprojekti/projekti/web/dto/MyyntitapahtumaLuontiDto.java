package ohjelmistoprojekti.projekti.web.dto;

import java.util.List;

/**
 * Myyntitapahtuman luontipyynnön runko. Tapahtumaan ja myyjään viitataan
 * pelkillä id:illä, koska niiden oletetaan olevan jo olemassa kannassa.
 * Myyntirivit annetaan samassa pyynnössä, jotta koko myynti syntyy yhtenä
 * atomisena kokonaisuutena.
 */
public class MyyntitapahtumaLuontiDto {

    private Long tapahtumaId;
    private Long kayttajaId;
    private List<MyyntiriviLuontiDto> rivit;

    public MyyntitapahtumaLuontiDto() {
    }

    public MyyntitapahtumaLuontiDto(Long tapahtumaId, Long kayttajaId, List<MyyntiriviLuontiDto> rivit) {
        this.tapahtumaId = tapahtumaId;
        this.kayttajaId = kayttajaId;
        this.rivit = rivit;
    }

    public Long getTapahtumaId() {
        return tapahtumaId;
    }

    public void setTapahtumaId(Long tapahtumaId) {
        this.tapahtumaId = tapahtumaId;
    }

    public Long getKayttajaId() {
        return kayttajaId;
    }

    public void setKayttajaId(Long kayttajaId) {
        this.kayttajaId = kayttajaId;
    }

    public List<MyyntiriviLuontiDto> getRivit() {
        return rivit;
    }

    public void setRivit(List<MyyntiriviLuontiDto> rivit) {
        this.rivit = rivit;
    }
}
