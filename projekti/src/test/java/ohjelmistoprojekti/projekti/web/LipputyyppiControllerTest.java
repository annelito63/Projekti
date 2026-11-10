package ohjelmistoprojekti.projekti.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LipputyyppiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Autowired
    private LipputyyppiRepository lipputyyppiRepository;

    private Tapahtuma tapahtuma;

    @BeforeEach
    void luoTapahtumaJaLipputyyppi() {
        tapahtuma = new Tapahtuma();
        tapahtuma.setNimi("Testikonsertti");
        tapahtuma.setTyyppi("Konsertti");
        tapahtuma.setAika("2026-12-10T18:00");
        tapahtuma.setKaupunki("Oulu");
        tapahtuma.setPaikka("Areena");
        tapahtuma.setMaara(100);
        tapahtuma = tapahtumaRepository.save(tapahtuma);

        Lipputyyppi lipputyyppi = new Lipputyyppi();
        lipputyyppi.setTapahtuma(tapahtuma);
        lipputyyppi.setNimi("Peruslippu");
        lipputyyppi.setKuvaus("Normaalihintainen lippu");
        lipputyyppi.setHinta(25.0);
        lipputyyppiRepository.save(lipputyyppi);
    }

    @Test
    void tapahtumanLipputyypitHaetaanJulkisesti() throws Exception {
        mockMvc.perform(get("/tapahtumat/{tapahtumaId}/lipputyypit", tapahtuma.getTapahtumaid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nimi").value("Peruslippu"))
                .andExpect(jsonPath("$[0].hinta").value(25.0))
                .andExpect(jsonPath("$[0].lipputyyppiId").isNumber());
    }

    @Test
    void tuntemattomanTapahtumanLipputyypitPalauttavat404() throws Exception {
        mockMvc.perform(get("/tapahtumat/{tapahtumaId}/lipputyypit", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void yllapitajaVoiLuodaLipputyypinTapahtumalle() throws Exception {
        mockMvc.perform(post("/tapahtumat/{tapahtumaId}/lipputyypit", tapahtuma.getTapahtumaid())
                .with(user("admin").roles("YLLAPITAJA"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Opiskelija\",\"kuvaus\":\"Opiskelijalippu\",\"hinta\":15.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nimi").value("Opiskelija"))
                .andExpect(jsonPath("$.hinta").value(15.5));

        mockMvc.perform(get("/tapahtumat/{tapahtumaId}/lipputyypit", tapahtuma.getTapahtumaid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void asiakasEiVoiLuodaLipputyyppia() throws Exception {
        mockMvc.perform(post("/tapahtumat/{tapahtumaId}/lipputyypit", tapahtuma.getTapahtumaid())
                .with(user("asiakas").roles("VERKKOKAUPAN_ASIAKAS"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Opiskelija\",\"kuvaus\":\"\",\"hinta\":15.5}"))
                .andExpect(status().isForbidden());
    }
}
