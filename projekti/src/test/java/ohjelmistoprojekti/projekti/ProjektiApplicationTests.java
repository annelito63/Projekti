package ohjelmistoprojekti.projekti;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ohjelmistoprojekti.projekti.event.EventController;
import ohjelmistoprojekti.projekti.event.EventService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProjektiApplicationTests {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(new EventController(new EventService()))
				.build();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void createsAndListsEvent() throws Exception {
		mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"name":"Kesäkonsertti","city":"Helsinki","venue":"Kaisaniemi","description":"Kesäinen ulkoilmakonsertti","capacity":500}
							"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Kesäkonsertti"))
				.andExpect(jsonPath("$.capacity").value(500));

		mockMvc.perform(get("/api/events"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Kesäkonsertti"));
	}

}
