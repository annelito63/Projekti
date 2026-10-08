package ohjelmistoprojekti.projekti.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ohjelmistoprojekti.projekti.model.Rooli;

/**
 * Käyttäjän tiedot sekä pyynnöissä (POST, PUT) että vastauksissa.
 * Entiteettiä ei palauteta suoraan, koska Kayttaja-luokan myynnit- ja
 * tarkastukset-listat sisältäisivät koko myyntihistorian. userId asetetaan
 * vain vastauksessa: pyynnön mukana tullut userId ohitetaan.
 * Salasanaa ei ole tarkoituksella vielä mukana - se lisätään vasta,
 * kun autentikaatio toteutetaan.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KayttajaDto {

    private Long userId;

    @NotBlank(message = "nimi on pakollinen")
    @Size(max = 100, message = "nimi saa olla enintään 100 merkkiä")
    private String nimi;

    @NotBlank(message = "sahkoposti on pakollinen")
    @Email(message = "sahkoposti ei ole kelvollinen")
    @Size(max = 254, message = "sahkoposti saa olla enintään 254 merkkiä")
    private String sahkoposti;

    @NotNull(message = "rooli on pakollinen")
    private Rooli rooli;

    public KayttajaDto() {
    }

    public KayttajaDto(Long userId, String nimi, String sahkoposti, Rooli rooli) {
        this.userId = userId;
        this.nimi = nimi;
        this.sahkoposti = sahkoposti;
        this.rooli = rooli;
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
}