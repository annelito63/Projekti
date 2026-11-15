package ohjelmistoprojekti.projekti.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

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
import ohjelmistoprojekti.projekti.model.LippuRepository;
import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Rooli;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MyyntitapahtumaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private LipputyyppiRepository lipputyyppiRepository;

    @Autowired
    private LippuRepository lippuRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long tapahtumaId;
    private Long kayttajaId;
    private Long lipputyyppiId;

    @BeforeEach
    void luoMyyntiinLiittyvatResurssit() {
		Tapahtuma tapahtuma = new Tapahtuma();
		tapahtuma.setNimi("Testitapahtuma");
		tapahtuma.setTyyppi("Konsertti");
		tapahtuma.setAika("2026-10-10T18:00");
		tapahtuma.setKaupunki("Oulu");
		tapahtuma.setPaikka("Areena");
		tapahtuma.setMaara(10);
		tapahtumaId = tapahtumaRepository.save(tapahtuma).getTapahtumaid();

        Lipputyyppi lipputyyppi = new Lipputyyppi();
        lipputyyppi.setTapahtuma(tapahtumaRepository.findById(tapahtumaId).orElseThrow());
        lipputyyppi.setNimi("Peruslippu");
        lipputyyppi.setHinta(20.0);
        lipputyyppiId = lipputyyppiRepository.save(lipputyyppi).getLipputyyppiId();

		Kayttaja kayttaja = new Kayttaja();
		kayttaja.setNimi("Testimyyja");
		kayttaja.setSahkoposti("testimyyja@example.test");
		kayttaja.setSalasanaHash(passwordEncoder.encode("Testisalasana123"));
		kayttaja.setRooli(Rooli.MYYJA);
		kayttajaId = kayttajaRepository.save(kayttaja).getUserId();
    }

	private ResultActions performAuthenticated(MockHttpServletRequestBuilder request) throws Exception {
		return mockMvc.perform(request.with(user("testimyyja").roles("MYYJA")));
	}

    @Test
    void tuntemattomanMyynninHakuPalauttaa404() throws Exception {
		performAuthenticated(get("/myynnit/{id}", Long.MAX_VALUE))
			.andExpect(status().isNotFound());
    }

	@Test
	void myynninHakuVirheellisellaIdTyypillaPalauttaa400() throws Exception {
		performAuthenticated(get("/myynnit/abc"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void myynninPaivitykseenKaytettyPutPalauttaa405() throws Exception {
		performAuthenticated(put("/myynnit/{id}", Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isMethodNotAllowed());
	}

    @Test
    void myynninLuontiVirheellisellaJsonillaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanTapahtumaIdtaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"kayttajaId\":1,\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanKayttajaIdtaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanRivejaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId + "}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTyhjallaRivilistallaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId
				+ ",\"rivit\":[]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTuntemattomallaTapahtumallaPalauttaa404() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + Long.MAX_VALUE + ",\"kayttajaId\":" + kayttajaId
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isNotFound());
    }

    @Test
    void myynninLuontiTuntemattomallaKayttajallaPalauttaa404() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + Long.MAX_VALUE
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isNotFound());
    }

    @Test
    void myynninLuontiRivillaIlmanLipputyyppiIdtaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"maara\":1}")))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiNollallaMaarallaPalauttaa400() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"lipputyyppiId\":1,\"maara\":0}")))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTuntemattomallaLipputyypillaPalauttaa404() throws Exception {
		performAuthenticated(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"lipputyyppiId\":" + Long.MAX_VALUE + ",\"maara\":1}")))
			.andExpect(status().isNotFound());
    }

    @Test
    void myyntiLuoYksilollisetLiputJaPalauttaaNiidenKoodit() throws Exception {
        performAuthenticated(post("/myynnit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithRow("{\"lipputyyppiId\":" + lipputyyppiId + ",\"maara\":2}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.liput.length()").value(2))
                .andExpect(jsonPath("$.liput[0].koodi").isString())
                .andExpect(jsonPath("$.liput[1].koodi").isString())
                .andExpect(jsonPath("$.kokonaissumma").value(40.0));

        assertEquals(2, lippuRepository.countByTapahtumaid(
                tapahtumaRepository.findById(tapahtumaId).orElseThrow()));
    }

    @Test
    void myyntiEiVoiYlittaaTapahtumanLippukapasiteettia() throws Exception {
        Tapahtuma tapahtuma = tapahtumaRepository.findById(tapahtumaId).orElseThrow();
        tapahtuma.setMaara(1);
        tapahtumaRepository.save(tapahtuma);

        performAuthenticated(post("/myynnit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithRow("{\"lipputyyppiId\":" + lipputyyppiId + ",\"maara\":2}")))
                .andExpect(status().isConflict());
    }

    @Test
    void myyntiEiVoiKayttaaToisenTapahtumanLipputyyppia() throws Exception {
        Tapahtuma toinenTapahtuma = new Tapahtuma();
        toinenTapahtuma.setNimi("Toinen tapahtuma");
        toinenTapahtuma.setTyyppi("Konsertti");
        toinenTapahtuma.setAika("2026-12-11T18:00");
        toinenTapahtuma.setKaupunki("Oulu");
        toinenTapahtuma.setPaikka("Toinen areena");
        toinenTapahtuma.setMaara(10);
        toinenTapahtuma = tapahtumaRepository.save(toinenTapahtuma);

        Lipputyyppi toinenTyyppi = new Lipputyyppi();
        toinenTyyppi.setTapahtuma(toinenTapahtuma);
        toinenTyyppi.setNimi("Toisen tapahtuman lippu");
        toinenTyyppi.setHinta(15.0);
        toinenTyyppi = lipputyyppiRepository.save(toinenTyyppi);

        performAuthenticated(post("/myynnit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithRow("{\"lipputyyppiId\":" + toinenTyyppi.getLipputyyppiId()
                        + ",\"maara\":1}")))
                .andExpect(status().isBadRequest());
    }

    private String requestWithRow(String row) {
		return "{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId
			+ ",\"rivit\":[" + row + "]}";
    }
}
