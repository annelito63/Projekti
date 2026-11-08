package ohjelmistoprojekti.projekti.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ohjelmistoprojekti.projekti.model.Lippu;
import ohjelmistoprojekti.projekti.model.LippuRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LippuControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private LippuRepository lippuRepository;

        private Long lippuId;
        private String koodi;

        @BeforeEach
        void luoLippu() {

                Lippu lippu = new Lippu();
                lippu.setKoodi("TEST123");
                lippu.setKuvaus("Testilippu");
                lippu.setHinta(49.90);
                lippu.setKaytetty(false);

                lippu = lippuRepository.save(lippu);

                lippuId = lippu.getId();
                koodi = lippu.getKoodi();
        }

        private ResultActions performAuthenticated(MockHttpServletRequestBuilder request) throws Exception {
                return mockMvc.perform(request.with(user("testimyyja").roles("MYYJA")));
        }

        @Test
        void lipunHakuKoodillaPalauttaaLipun() throws Exception {

                performAuthenticated(
                                get("/ticket/ticket")
                                                .param("koodi", koodi))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(lippuId))
                                .andExpect(jsonPath("$.koodi").value("TEST123"))
                                .andExpect(jsonPath("$.kuvaus").value("Testilippu"))
                                .andExpect(jsonPath("$.hinta").value(49.90))
                                .andExpect(jsonPath("$.kaytetty").value(false));
        }

        @Test
        void loytaaLipunKoodilla() {

                Optional<Lippu> result = lippuRepository.findByKoodi("TEST123");

                assertTrue(result.isPresent());
                assertEquals("TEST123", result.get().getKoodi());
        }

        @Test
        void lipunMerkitseminenKaytetyksiTallentuuTietokantaan() throws Exception {

                performAuthenticated(
                                patch("/ticket/{id}", lippuId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "kaytetty": true
                                                                }
                                                                """))
                                .andExpect(status().isOk());

                Lippu paivitetty = lippuRepository.findById(lippuId).orElseThrow();

                assertTrue(paivitetty.isKaytetty());
        }

        @Test
        void tuntemattomanLipunHakuPalauttaa404() throws Exception {

                performAuthenticated(
                                get("/ticket/ticket")
                                                .param("koodi", "EI_LOYDY"))

                                .andExpect(status().isNotFound());
        }

        @Test
        void lipunHakuIlmanKoodiaPalauttaa400() throws Exception {

                performAuthenticated(
                                get("/ticket/ticket"))

                                .andExpect(status().isBadRequest());
        }

        @Test
        void lipunMerkitseminenKaytetyksiOnnistuu() throws Exception {

                performAuthenticated(
                                patch("/ticket/{id}", lippuId)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "kaytetty": true
                                                                }
                                                                """))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(lippuId))
                                .andExpect(jsonPath("$.kaytetty").value(true));
        }

        @Test
        void lipunPaivitysTuntemattomallaIdllaPalauttaa404() throws Exception {

                performAuthenticated(
                                patch("/ticket/{id}", Long.MAX_VALUE)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "kaytetty": true
                                                                }
                                                                """))

                                .andExpect(status().isNotFound());
        }

        @Test
        void lipunPaivitysVirheellisellaIdTyypillaPalauttaa400() throws Exception {

                performAuthenticated(
                                patch("/ticket/abc")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "kaytetty": true
                                                                }
                                                                """))

                                .andExpect(status().isBadRequest());
        }

        @Test
        void lipunPaivitysVirheellisellaJsonillaPalauttaa400() throws Exception {

                performAuthenticated(
                                patch("/ticket/1")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{"))

                                .andExpect(status().isBadRequest());
        }
}