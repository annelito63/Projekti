package ohjelmistoprojekti.projekti.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import ohjelmistoprojekti.projekti.model.Kayttaja;
import ohjelmistoprojekti.projekti.model.KayttajaRepository;
import ohjelmistoprojekti.projekti.model.Rooli;

/**
 * Testaa, että ensimmäinen ylläpitäjä luodaan käynnistyksessä
 * ympäristömuuttujista
 * ja että uudelleenkäynnistys (pysyvässä kannassa) ei kaada sovellusta.
 */
@SpringBootTest(properties = {
        "app.bootstrap-admin.email=Bootstrap@Example.test",
        "app.bootstrap-admin.password=BootstrapSalasana123",
        "app.bootstrap-admin.name=Bootstrap Admin"
})
@AutoConfigureMockMvc
class BootstrapAdminTest {

    private static final String EMAIL = "bootstrap@example.test";
    private static final String PASSWORD = "BootstrapSalasana123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private ApplicationContext context;

    @Test
    void yllapitajaLuodaanKaynnistyksessaTiivistetyllaSalasanalla() {
        Kayttaja admin = kayttajaRepository.findBySahkopostiIgnoreCase(EMAIL).orElseThrow();

        assertEquals(Rooli.YLLAPITAJA, admin.getRooli());
        assertEquals("Bootstrap Admin", admin.getNimi());
        assertNotEquals(PASSWORD, admin.getSalasanaHash());
        assertTrue(admin.getSalasanaHash().startsWith("$2"), "Salasanan pitää olla BCrypt-tiiviste");
    }

    @Test
    void bootstrapYllapitajaPaseeKirjautumaan() throws Exception {
        mockMvc.perform(get("/auth/me").with(httpBasic(EMAIL, PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void vaaraSalasanaEiKelpaa() throws Exception {
        mockMvc.perform(get("/auth/me").with(httpBasic(EMAIL, "vaara-salasana")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uudelleenkaynnistysEiLuoDuplikaattiaEikaKaadu() throws Exception {
        CommandLineRunner runner = context.getBean("createBootstrapAdmin", CommandLineRunner.class);

        runner.run(); // simuloi toista käynnistystä samaa kantaa vasten

        assertTrue(kayttajaRepository.findBySahkopostiIgnoreCase(EMAIL).isPresent());
    }
}
