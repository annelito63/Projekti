package ohjelmistoprojekti.projekti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ohjelmistoprojekti.projekti.model.Lippu;
import ohjelmistoprojekti.projekti.model.LippuRepository;

@SpringBootTest
class ProjektiApplicationTests {

	@Autowired
	private LippuRepository lippuRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void LipunHakeminenJaAtribuuttienLukeminen() {
		Lippu tallennettavaLippu = new Lippu(null, null, "VIP-lippu", "Pääsy tapahtuman VIP-alueelle", 49.90,
				null);
		Lippu tallennettuLippu = lippuRepository.save(tallennettavaLippu);

		Lippu haettuLippu = lippuRepository.findByNimi("VIP-lippu").stream()
				.findFirst()
				.orElseThrow();

		System.out.println("Haettu lippu: " + haettuLippu);
		assertNotNull(haettuLippu.getId());
		assertEquals(tallennettuLippu.getId(), haettuLippu.getId());
		assertEquals("VIP-lippu", haettuLippu.getNimi());
		assertEquals("Pääsy tapahtuman VIP-alueelle", haettuLippu.getKuvaus());
		assertEquals(49.90, haettuLippu.getHinta());
		assertNull(haettuLippu.getTapahtumaid());
		assertNull(haettuLippu.getLipputyyppi());
	}

}
