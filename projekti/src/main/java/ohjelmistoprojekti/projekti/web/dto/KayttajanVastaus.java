package ohjelmistoprojekti.projekti.web.dto;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.Rooli;

public record KayttajanVastaus(Long userId, String nimi, String sahkoposti, Rooli rooli) {

    public static KayttajanVastaus from(Kayttaja kayttaja) {
        return new KayttajanVastaus(
                kayttaja.getUserId(),
                kayttaja.getNimi(),
                kayttaja.getSahkoposti(),
                kayttaja.getRooli());
    }
}
