package ohjelmistoprojekti.projekti.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

/**
 * HTTP-tason testit tapahtumien onnistuneille poluille ja oikeuksille.
 * Täydentää TapahtumaControllerTestiä, joka testaa pääosin virhetilanteita.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TapahtumaHttpTest {

        private static final String VALIDI_JSON = "{\"nimi\":\"Päivitetty tapahtuma\",\"tyyppi\":\"Konsertti\","
                        + "\"aika\":\"2026-12-01T18:00\",\"kaupunki\":\"Helsinki\",\"paikka\":\"Testihalli\","
                        + "\"kuvaus\":\"Päivitetty kuvaus\",\"maara\":50}";

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private TapahtumaRepository tapahtumaRepository;

        private Long tapahtumaId;

        @BeforeEach
        void luoTapahtuma() {
                Tapahtuma tapahtuma = new Tapahtuma();
                tapahtuma.setNimi("Alkuperäinen tapahtuma");
                tapahtuma.setTyyppi("Konsertti");
                tapahtuma.setAika("2026-10-10T18:00");
                tapahtuma.setKaupunki("Oulu");
                tapahtuma.setPaikka("Areena");
                tapahtuma.setMaara(25);
                tapahtumaId = tapahtumaRepository.save(tapahtuma).getTapahtumaid();
        }

        // --- Julkiset lukureitit (verkkokauppaa varten) ---

        @Test
        void tapahtumalistausOnJulkinenIlmanKirjautumista() throws Exception {
                mockMvc.perform(get("/tapahtumat"))
                                .andExpect(status().isOk());
        }

        @Test
        void yksittainenTapahtumaOnJulkinenIlmanKirjautumista() throws Exception {
                mockMvc.perform(get("/tapahtumat/{id}", tapahtumaId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nimi").value("Alkuperäinen tapahtuma"));
        }

        // --- Kirjoitusoperaatiot ilman oikeuksia ---

        @Test
        void kirjautumatonEiVoiLuodaMuokataTaiPoistaaTapahtumaa() throws Exception {
                mockMvc.perform(post("/tapahtumat")
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().isUnauthorized());
                mockMvc.perform(put("/tapahtumat/{id}", tapahtumaId)
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().isUnauthorized());
                mockMvc.perform(delete("/tapahtumat/{id}", tapahtumaId))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void myyjaEiVoiLuodaMuokataTaiPoistaaTapahtumaa() throws Exception {
                mockMvc.perform(post("/tapahtumat").with(user("myyja").roles("MYYJA"))
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().isForbidden());
                mockMvc.perform(put("/tapahtumat/{id}", tapahtumaId).with(user("myyja").roles("MYYJA"))
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().isForbidden());
                mockMvc.perform(delete("/tapahtumat/{id}", tapahtumaId).with(user("myyja").roles("MYYJA")))
                                .andExpect(status().isForbidden());
                // Tapahtuma ei muuttunut
                assertEquals("Alkuperäinen tapahtuma",
                                tapahtumaRepository.findById(tapahtumaId).orElseThrow().getNimi());
        }

        // --- Ylläpitäjän onnistuneet toiminnot HTTP:n kautta ---

        @Test
        void yllapitajaVoiLuodaTapahtuman() throws Exception {
                long ennen = tapahtumaRepository.count();

                mockMvc.perform(post("/tapahtumat").with(user("admin").roles("YLLAPITAJA"))
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().is2xxSuccessful());

                assertEquals(ennen + 1, tapahtumaRepository.count());
        }

        @Test
        void yllapitajaVoiPaivittaaTapahtuman() throws Exception {
                mockMvc.perform(put("/tapahtumat/{id}", tapahtumaId).with(user("admin").roles("YLLAPITAJA"))
                                .contentType(MediaType.APPLICATION_JSON).content(VALIDI_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nimi").value("Päivitetty tapahtuma"))
                                .andExpect(jsonPath("$.maara").value(50));

                Tapahtuma paivitetty = tapahtumaRepository.findById(tapahtumaId).orElseThrow();
                assertEquals("Päivitetty kuvaus", paivitetty.getKuvaus());
                assertEquals(50, paivitetty.getMaara());
        }

        @Test
        void yllapitajaVoiPoistaaTapahtuman() throws Exception {
                mockMvc.perform(delete("/tapahtumat/{id}", tapahtumaId).with(user("admin").roles("YLLAPITAJA")))
                                .andExpect(status().isNoContent());

                assertFalse(tapahtumaRepository.findById(tapahtumaId).isPresent());
                mockMvc.perform(get("/tapahtumat/{id}", tapahtumaId))
                                .andExpect(status().isNotFound());
        }
}
