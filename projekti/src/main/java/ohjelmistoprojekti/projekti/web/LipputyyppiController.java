package ohjelmistoprojekti.projekti.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;

@RestController
@CrossOrigin(origins = "*")
public class LipputyyppiController {

    private final LipputyyppiRepository lipputyyppiRepository;
    private final TapahtumaRepository tapahtumaRepository;

    public LipputyyppiController(LipputyyppiRepository lipputyyppiRepository,
            TapahtumaRepository tapahtumaRepository) {
        this.lipputyyppiRepository = lipputyyppiRepository;
        this.tapahtumaRepository = tapahtumaRepository;
    }

    @GetMapping("/tapahtumat/{tapahtumaId}/lipputyypit")
    public ResponseEntity<List<LipputyyppiVastaus>> findByTapahtuma(@PathVariable Long tapahtumaId) {
        Tapahtuma tapahtuma = tapahtumaRepository.findById(tapahtumaId).orElse(null);
        if (tapahtuma == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<LipputyyppiVastaus> lipputyypit = lipputyyppiRepository.findByTapahtuma(tapahtuma).stream()
                .map(LipputyyppiVastaus::from)
                .toList();
        return ResponseEntity.ok(lipputyypit);
    }

    public record LipputyyppiVastaus(Long lipputyyppiId, String nimi, String kuvaus, double hinta) {
        private static LipputyyppiVastaus from(Lipputyyppi lipputyyppi) {
            return new LipputyyppiVastaus(lipputyyppi.getLipputyyppiId(), lipputyyppi.getNimi(),
                    lipputyyppi.getKuvaus(), lipputyyppi.getHinta());
        }
    }
}
