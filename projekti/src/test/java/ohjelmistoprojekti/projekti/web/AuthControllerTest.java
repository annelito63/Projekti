package ohjelmistoprojekti.projekti.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Myyntitapahtuma;
import ohjelmistoprojekti.projekti.model.MyyntitapahtumaRepository;
import ohjelmistoprojekti.projekti.model.Rooli;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private TapahtumaRepository tapahtumaRepository;

    @Autowired
    private LipputyyppiRepository lipputyyppiRepository;

    @Autowired
    private MyyntitapahtumaRepository myyntitapahtumaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Kayttaja asiakas;
    private Kayttaja toinenAsiakas;
    private Tapahtuma tapahtuma;
    private Lipputyyppi lipputyyppi;

    @BeforeEach
    void luoTestiasiakkaatJaTapahtuman() {
        asiakas = luoKayttaja("asiakas@example.test", Rooli.VERKKOKAUPAN_ASIAKAS);
        toinenAsiakas = luoKayttaja("toinen@example.test", Rooli.VERKKOKAUPAN_ASIAKAS);
        luoKayttaja("yllapitaja@example.test", Rooli.YLLAPITAJA);

        tapahtuma = new Tapahtuma();
        tapahtuma.setNimi("Testitapahtuma");
        tapahtuma.setTyyppi("Konsertti");
        tapahtuma.setAika("2026-10-10T18:00");
        tapahtuma.setKaupunki("Oulu");
        tapahtuma.setPaikka("Areena");
        tapahtuma.setMaara(100);
        tapahtuma = tapahtumaRepository.save(tapahtuma);

        lipputyyppi = new Lipputyyppi();
        lipputyyppi.setTapahtuma(tapahtuma);
        lipputyyppi.setNimi("Peruslippu");
        lipputyyppi.setHinta(20.0);
        lipputyyppi = lipputyyppiRepository.save(lipputyyppi);

        myyntitapahtumaRepository.save(luoMyynti(asiakas));
        myyntitapahtumaRepository.save(luoMyynti(toinenAsiakas));
    }

    @Test
    void kirjautumatonEiVoiNähdäMyyntejä() throws Exception {
        mockMvc.perform(get("/myynnit"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void asiakasNäkeeVainOmatMyyntinsä() throws Exception {
        mockMvc.perform(get("/myynnit")
                .with(httpBasic("asiakas@example.test", "Testisalasana123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].kayttaja.userId").value(asiakas.getUserId()))
                .andExpect(jsonPath("$[0].kayttaja.salasanaHash").doesNotExist());
    }

    @Test
    void asiakasEiVoiHakeaToisenMyyntia() throws Exception {
        Long toisenMyyntiId = myyntitapahtumaRepository.findByKayttaja(toinenAsiakas).get(0).getMyyntiId();

        mockMvc.perform(get("/myynnit/{id}", toisenMyyntiId)
                .with(httpBasic("asiakas@example.test", "Testisalasana123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void asiakkaanMyyntiLiitetaanKirjautuneeseenKayttajaanVaikkaIdVaarennetaan() throws Exception {
        mockMvc.perform(post("/myynnit")
                .with(httpBasic("asiakas@example.test", "Testisalasana123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tapahtumaId\":" + tapahtuma.getTapahtumaid()
                        + ",\"kayttajaId\":" + toinenAsiakas.getUserId()
                        + ",\"rivit\":[{\"lipputyyppiId\":" + lipputyyppi.getLipputyyppiId()
                        + ",\"maara\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kayttaja.userId").value(asiakas.getUserId()));
    }

    @Test
    void asiakasVoiNähdäTapahtumaluettelonMuttaEiLuodaTapahtumaa() throws Exception {
        mockMvc.perform(get("/tapahtumat")
                .with(httpBasic("asiakas@example.test", "Testisalasana123")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/tapahtumat")
                .with(httpBasic("asiakas@example.test", "Testisalasana123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Uusi\",\"tyyppi\":\"Konsertti\",\"aika\":\"2026-12-01\","
                        + "\"kaupunki\":\"Oulu\",\"paikka\":\"Areena\",\"maara\":10}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void yllapitajaVoiNähdaKaikkienMyynnit() throws Exception {
        mockMvc.perform(get("/myynnit")
                .with(httpBasic("yllapitaja@example.test", "Testisalasana123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void asiakasEiVoiLuodaHenkilostokayttajia() throws Exception {
        mockMvc.perform(post("/auth/users")
                .with(httpBasic("asiakas@example.test", "Testisalasana123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Admin\",\"sahkoposti\":\"admin@example.test\","
                        + "\"salasana\":\"PitkaSalasana123\",\"rooli\":\"YLLAPITAJA\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rekisterointiLuoAsiakkaanEikaPalautaSalasanaa() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Uusi asiakas\",\"sahkoposti\":\"uusi@example.test\","
                        + "\"salasana\":\"PitkaSalasana123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rooli").value("VERKKOKAUPAN_ASIAKAS"))
                .andExpect(jsonPath("$.salasanaHash").doesNotExist());
    }

    @Test
    void rekisterointiHylkaaBcryptinTavurajanYlittavanSalasanan() throws Exception {
        String liianPitkaUtf8Salasana = "ä".repeat(40);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Uusi asiakas\",\"sahkoposti\":\"utf8@example.test\","
                        + "\"salasana\":\"" + liianPitkaUtf8Salasana + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rekisterointiEiVoiKorottaaRooliaTaiKayttaaSamaaSahkopostia() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Uusi\",\"sahkoposti\":\"uusi@example.test\","
                        + "\"salasana\":\"PitkaSalasana123\",\"rooli\":\"YLLAPITAJA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rooli").value("VERKKOKAUPAN_ASIAKAS"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nimi\":\"Uusi\",\"sahkoposti\":\"UUSI@example.test\","
                        + "\"salasana\":\"PitkaSalasana123\"}"))
                .andExpect(status().isConflict());
    }

    private Kayttaja luoKayttaja(String email, Rooli rooli) {
        Kayttaja kayttaja = new Kayttaja();
        kayttaja.setNimi(email);
        kayttaja.setSahkoposti(email);
        kayttaja.setSalasanaHash(passwordEncoder.encode("Testisalasana123"));
        kayttaja.setRooli(rooli);
        return kayttajaRepository.save(kayttaja);
    }

    private Myyntitapahtuma luoMyynti(Kayttaja kayttaja) {
        return new Myyntitapahtuma(null, tapahtuma, kayttaja, LocalDateTime.now(), 20.0, List.of());
    }
}
