package ohjelmistoprojekti.projekti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@SpringBootTest
class TapahtumaControllerTest {

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Test
    void tapahtumaPystyyPaivittamaanJaPoistamaan() {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setNimi("Testitapahtuma");
        tapahtuma.setTyyppi("Konsertti");
        tapahtuma.setAika("2026-10-10T18:00");
        tapahtuma.setKaupunki("Oulu");
        tapahtuma.setPaikka("Areena");
        tapahtuma.setKuvaus("Alkuperäinen kuvaus");
        tapahtuma.setMaara(25);

        Tapahtuma tallennettu = tapahtumaRepository.save(tapahtuma);
        assertNotNull(tallennettu.getTapahtumaid());

        Optional<Tapahtuma> haettu = tapahtumaRepository.findById(tallennettu.getTapahtumaid());
        assertEquals("Testitapahtuma", haettu.orElseThrow().getNimi());

        haettu.get().setNimi("Päivitetty tapahtuma");
        haettu.get().setKuvaus("Päivitetty kuvaus");
        haettu.get().setMaara(40);
        Tapahtuma paivitetty = tapahtumaRepository.save(haettu.get());

        assertEquals("Päivitetty tapahtuma", paivitetty.getNimi());
        assertEquals("Päivitetty kuvaus", paivitetty.getKuvaus());
        assertEquals(40, paivitetty.getMaara());

        tapahtumaRepository.deleteById(paivitetty.getTapahtumaid());
        assertFalse(tapahtumaRepository.findById(paivitetty.getTapahtumaid()).isPresent());
    }
}
