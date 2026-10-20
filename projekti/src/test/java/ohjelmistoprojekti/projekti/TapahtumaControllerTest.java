package ohjelmistoprojekti.projekti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Rooli;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TapahtumaControllerTest {

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void luoYllapitaja() {
        Kayttaja kayttaja = new Kayttaja();
        kayttaja.setNimi("Testiylläpitäjä");
        kayttaja.setSahkoposti("testiyllapitaja@example.test");
        kayttaja.setSalasanaHash(passwordEncoder.encode("Testisalasana123"));
        kayttaja.setRooli(Rooli.YLLAPITAJA);
        kayttajaRepository.save(kayttaja);
    }

    private ResultActions performAuthenticated(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request.with(user("testiyllapitaja").roles("YLLAPITAJA")));
    }

    @Test
    void tapahtumanHakeminenTuntemattomallaIdllaPalauttaa404() throws Exception {
        performAuthenticated(get("/tapahtumat/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void tapahtumanHakuVirheellisellaIdTyypillaPalauttaa400() throws Exception {
        performAuthenticated(get("/tapahtumat/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tapahtumanLuontiVirheellisellaJsonillaPalauttaa400() throws Exception {
        performAuthenticated(post("/tapahtumat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tapahtumanLuontiVaarallaSisaltotyypillaPalauttaa415() throws Exception {
        performAuthenticated(post("/tapahtumat")
                .contentType(MediaType.TEXT_PLAIN)
                .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void resurssipolunPostPalauttaa405() throws Exception {
        performAuthenticated(post("/tapahtumat/{id}", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void tapahtumanLuontiPuuttuvillaPakollisillaKentillaPalauttaa400() throws Exception {
        performAuthenticated(post("/tapahtumat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Tapahtuma\",\"tyyppi\":\"Konsertti\","
                        + "\"aika\":\"2026-10-10T18:00\",\"kaupunki\":\"Oulu\",\"maara\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tapahtumanPaivitysTuntemattomallaIdllaPalauttaa404() throws Exception {
        performAuthenticated(put("/tapahtumat/{id}", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Tapahtuma\",\"tyyppi\":\"Konsertti\","
                        + "\"aika\":\"2026-10-10T18:00\",\"kaupunki\":\"Oulu\","
                        + "\"paikka\":\"Areena\",\"maara\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tapahtumanPaivitysPuuttuvillaPakollisillaKentillaPalauttaa400() throws Exception {
        Tapahtuma tapahtuma = new Tapahtuma();
        tapahtuma.setNimi("Testitapahtuma");
        tapahtuma.setTyyppi("Konsertti");
        tapahtuma.setAika("2026-10-10T18:00");
        tapahtuma.setKaupunki("Oulu");
        tapahtuma.setPaikka("Areena");
        Long id = tapahtumaRepository.save(tapahtuma).getTapahtumaid();

        performAuthenticated(put("/tapahtumat/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Tapahtuma\",\"tyyppi\":\"Konsertti\","
                        + "\"aika\":\"2026-10-10T18:00\",\"kaupunki\":\"Oulu\",\"maara\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tapahtumanPoistoTuntemattomallaIdllaPalauttaa404() throws Exception {
        performAuthenticated(delete("/tapahtumat/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

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
