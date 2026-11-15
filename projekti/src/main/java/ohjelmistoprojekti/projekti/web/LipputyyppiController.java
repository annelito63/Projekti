package ohjelmistoprojekti.projekti.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import ohjelmistoprojekti.projekti.model.Lipputyyppi;
import ohjelmistoprojekti.projekti.model.LipputyyppiRepository;
import ohjelmistoprojekti.projekti.model.Tapahtuma;
import ohjelmistoprojekti.projekti.model.TapahtumaRepository;
import ohjelmistoprojekti.projekti.web.dto.LipputyyppiLuontiPyynto;

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

    @PostMapping("/tapahtumat/{tapahtumaId}/lipputyypit")
    @PreAuthorize("hasRole('YLLAPITAJA')")
    public ResponseEntity<LipputyyppiVastaus> create(
            @PathVariable Long tapahtumaId,
            @Valid @RequestBody LipputyyppiLuontiPyynto pyynto) {
        Tapahtuma tapahtuma = tapahtumaRepository.findById(tapahtumaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tapahtumaa ei löytynyt"));

        Lipputyyppi lipputyyppi = new Lipputyyppi();
        lipputyyppi.setTapahtuma(tapahtuma);
        lipputyyppi.setNimi(pyynto.nimi().trim());
        lipputyyppi.setKuvaus(pyynto.kuvaus());
        lipputyyppi.setHinta(pyynto.hinta().doubleValue());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LipputyyppiVastaus.from(lipputyyppiRepository.save(lipputyyppi)));
    }

    public record LipputyyppiVastaus(Long lipputyyppiId, String nimi, String kuvaus, double hinta) {
        private static LipputyyppiVastaus from(Lipputyyppi lipputyyppi) {
            return new LipputyyppiVastaus(lipputyyppi.getLipputyyppiId(), lipputyyppi.getNimi(),
                    lipputyyppi.getKuvaus(), lipputyyppi.getHinta());
        }
    }
}
