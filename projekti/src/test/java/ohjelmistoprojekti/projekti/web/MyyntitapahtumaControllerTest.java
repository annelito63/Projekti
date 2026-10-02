package ohjelmistoprojekti.projekti.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
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

    private Long tapahtumaId;
    private Long kayttajaId;

    @BeforeEach
    void luoMyyntiinLiittyvatResurssit() {
		Tapahtuma tapahtuma = new Tapahtuma();
		tapahtuma.setNimi("Testitapahtuma");
		tapahtuma.setTyyppi("Konsertti");
		tapahtuma.setAika("2026-10-10T18:00");
		tapahtuma.setKaupunki("Oulu");
		tapahtuma.setPaikka("Areena");
		tapahtumaId = tapahtumaRepository.save(tapahtuma).getTapahtumaid();

		Kayttaja kayttaja = new Kayttaja();
		kayttaja.setNimi("Testimyyja");
		kayttaja.setRooli(Rooli.MYYJA);
		kayttajaId = kayttajaRepository.save(kayttaja).getUserId();
    }

    @Test
    void tuntemattomanMyynninHakuPalauttaa404() throws Exception {
		mockMvc.perform(get("/myynnit/{id}", Long.MAX_VALUE))
			.andExpect(status().isNotFound());
    }

	@Test
	void myynninHakuVirheellisellaIdTyypillaPalauttaa400() throws Exception {
		mockMvc.perform(get("/myynnit/abc"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void myynninPaivitykseenKaytettyPutPalauttaa405() throws Exception {
		mockMvc.perform(put("/myynnit/{id}", Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isMethodNotAllowed());
	}

    @Test
    void myynninLuontiVirheellisellaJsonillaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanTapahtumaIdtaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"kayttajaId\":1,\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanKayttajaIdtaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiIlmanRivejaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId + "}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTyhjallaRivilistallaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId
				+ ",\"rivit\":[]}"))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTuntemattomallaTapahtumallaPalauttaa404() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + Long.MAX_VALUE + ",\"kayttajaId\":" + kayttajaId
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isNotFound());
    }

    @Test
    void myynninLuontiTuntemattomallaKayttajallaPalauttaa404() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + Long.MAX_VALUE
				+ ",\"rivit\":[{\"lipputyyppiId\":1,\"maara\":1}]}"))
			.andExpect(status().isNotFound());
    }

    @Test
    void myynninLuontiRivillaIlmanLipputyyppiIdtaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"maara\":1}")))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiNollallaMaarallaPalauttaa400() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"lipputyyppiId\":1,\"maara\":0}")))
			.andExpect(status().isBadRequest());
    }

    @Test
    void myynninLuontiTuntemattomallaLipputyypillaPalauttaa404() throws Exception {
		mockMvc.perform(post("/myynnit")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestWithRow("{\"lipputyyppiId\":" + Long.MAX_VALUE + ",\"maara\":1}")))
			.andExpect(status().isNotFound());
    }

    private String requestWithRow(String row) {
		return "{\"tapahtumaId\":" + tapahtumaId + ",\"kayttajaId\":" + kayttajaId
			+ ",\"rivit\":[" + row + "]}";
    }
}
